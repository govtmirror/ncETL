'''
Fetch the aggregated precipitation data for one month in the form of a NetCDF file

@author: rhayes
'''

from datetime import date,datetime
from dateutil.relativedelta import relativedelta
import fetchAggregate
import os
import argparse
import logging
import sys
import requests

basedir='/mnt/thredds-optimized/rfc_qpe'

def fetch(d, rfc, basedir, dryrun=False):
    logging.info("Aggregating month %s for RFC %s", d.strftime('%Y-%m'), rfc.upper())
    
    destDir = os.path.join(basedir,'archive/all_rfc/harvest')
    try:
        if dryrun:
            _varName, rfcId = fetchAggregate.discoverMetadata(rfc)
            filename = fetchAggregate.makeArchiveName(d, rfcId, destDir)
            agg = "Would fetch aggregate %s for %s to %s" % (rfc, d.strftime('%Y-%m'), filename)
            logging.info(agg)
        else: 
            agg = fetchAggregate.fetchAggregate(rfc, d.strftime('%Y-%m'), destDir=destDir)
            logging.info("Wrote aggregate as %s", agg)
            os.system("ncks -O --fix_rec_dmn "+agg+" "+agg)
            logging.info("Fixed time dimension of %s with 'ncks -O --fix_rec_dmn'", agg)
    except requests.exceptions.RequestException as e:
        logging.warn("Failed to fetch for %s because %s", rfc, e)
        agg = e
        
    return agg

def aggregate(d, basedir=basedir, rfc='all', dryrun=False):
    if rfc == 'all':
        rfcs = ['tsju','ktua','kstr','krsa','korn','krha','kkrf','kmsr','ktar','kptr','ktir','kalr','kfwr']
    else:
        rfcs = [rfc]
        
    for rfc in rfcs:
        fetch(d, rfc, basedir,dryrun=dryrun)

def _checkMonth(s):
    return datetime.strptime(s, '%Y-%m')

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Aggregate precipitation data from Thredds for a particular month')
    parser.add_argument('--basedir', default=argparse.SUPPRESS, help="Base directory for THREDDS files")
    parser.add_argument("--rfc", default=argparse.SUPPRESS, help="River Forecasting Center 4-letter name, default all")
    parser.add_argument('--thredds', help="Base URL for THREDDS server")
    parser.add_argument('--dryrun', action="store_true")
    _checkMonth.__name__ = "month"
    parser.add_argument('month',help="Month to aggregate, defaults to previous month",nargs="?",default=None, type=_checkMonth)
    parser.add_argument('--loglevel', choices=['CRITICAL','ERROR','WARNING', 'INFO', 'DEBUG'], default='INFO')

    args = parser.parse_args()
    optargs = vars(args)
    month = optargs.pop("month")
    # compute default month
    if not month:
        month = date.today() + relativedelta(months=-2)

    thredds = optargs.pop("thredds")
    if thredds:
        fetchAggregate.thredds = thredds
    
    loglevel = optargs.pop('loglevel')
    numeric_level = getattr(logging, loglevel.upper(), logging.INFO)
    logfile = month.strftime('aggregate_%Y-%m.log')
    logging.basicConfig(format='%(asctime)s %(levelname)s:%(message)s',filename=logfile, level=numeric_level)
    
    logging.getLogger().addHandler(logging.StreamHandler(sys.stdout))
    
    aggregate(month,**optargs)
  
    
    
