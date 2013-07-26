'''
Created on Jul 25, 2013

@author: rhayes
'''

from datetime import date,datetime
from dateutil.relativedelta import relativedelta
import fetchAggregate
import glob
import os
import argparse

def fetchThenClean(d, basedir='/mnt/thredds-data-00/rfc_qpe',rfc='all',dryrun=False):
    if not d:
        d = date.today() + relativedelta(months=-1)

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
    parser.add_argument('--dryrun', action="store_true")
    _checkMonth.__name__ = "month"
    parser.add_argument('month',help="Month to aggregate, defaults to previous month",nargs="?",default=None, type=_checkMonth)
    
    args = parser.parse_args()
    optargs = vars(args)
    month = optargs.pop("month")
    fetchThenClean(month,**optargs)
    

