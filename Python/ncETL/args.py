'''
Created on Jul 25, 2013

@author: rhayes
'''

from datetime import date,datetime
from dateutil.relativedelta import relativedelta
import argparse
import logging

def fetchThenClean(d, basedir='/mnt/thredds-data-00/rfc_qpe',rfc='all',dryrun=False):
    if not d:
        d = date.today() + relativedelta(months=-1)

    logging.critical("in fetchThenClean(%s)", d)
    logging.error("in fetchThenClean(%s)", d)
    logging.warn("in fetchThenClean(%s)", d)
    logging.info("in fetchThenClean(%s)", d)
    logging.debug("in fetchThenClean(%s)", d)
    
    print "aggregate month", d.strftime('%Y-%m')
    print "rfc", rfc
    print "dryrun", dryrun
    print "basedir", basedir

def _checkMonth(s):
    return datetime.strptime(s, '%Y-%m')

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Aggregate grid data from Thredds for a particular month')
    parser.add_argument('--basedir', default=argparse.SUPPRESS, help="Base directory for THREDDS files")
    parser.add_argument("--rfc", default=argparse.SUPPRESS, help="River Forecasting Center 4-letter name, default all")
    parser.add_argument('--thredds', help="Base URL for THREDDS server")
    parser.add_argument('--dryrun', action="store_true")

    _checkMonth.__name__ = "month"
    parser.add_argument('month',help="Month to aggregate, defaults to previous month",nargs="?",default=None, type=_checkMonth)
    parser.add_argument('--loglevel', choices=['CRITICAL','ERROR','WARNING', 'INFO', 'DEBUG'], default='INFO')

    args = parser.parse_args()
    optargs = vars(args)
    loglevel = optargs.pop('loglevel')
    numeric_level = getattr(logging, loglevel.upper(), logging.INFO)
    logging.basicConfig(format='%(asctime)s %(levelname)s:%(message)s', level=numeric_level)
    
    month = optargs.pop("month")
    thredds = optargs.pop("thredds")
    if thredds:
        print "set thredds", thredds
    fetchThenClean(month,**optargs)
    

