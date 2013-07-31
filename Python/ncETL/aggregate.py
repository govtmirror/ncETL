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

basedir='/mnt/thredds-data-00/rfc_qpe'

def fetch(d, rfc, basedir, dryrun=False):
    logging.info("Aggregating month %s for RFC %s", d.strftime('%Y-%m'), rfc.upper())
    
    destDir = os.path.join(basedir,'archive/all_rfc/harvest')
    if dryrun:
        agg = "Would fetch aggregate %s for %s to %s" % (rfc, d.strftime('%Y-%m'), destDir)
        logging.info(agg)
    else: 
        agg = fetchAggregate.fetchAggregate(rfc, d.strftime('%Y-%m'), destDir=destDir)
        logging.info("Wrote aggregate as %s", agg)

    return agg

def aggregate(d, basedir=basedir, rfc='all', dryrun=False):
    # get one month's aggregation
    if not d:
        d = date.today() + relativedelta(months=-1)
    
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
    thredds = optargs.pop("thredds")
    if thredds:
        fetchAggregate.thredds = thredds
    
    loglevel = optargs.pop('loglevel')
    numeric_level = getattr(logging, loglevel.upper(), logging.INFO)
    logging.basicConfig(format='%(asctime)s %(levelname)s:%(message)s',filename='aggregate.log', level=numeric_level)
    
    logging.getLogger().addHandler(logging.StreamHandler(sys.stdout))
    
    aggregate(month,**optargs)
  
    
    
