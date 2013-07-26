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

def fetchThenClean(d, threddsBase):
    print "aggregate month ", d.strftime('%Y-%m')

    print d, threddsBase

def _checkMonth(s):
    return datetime.strptime(s, '%Y-%m')

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Aggregate grid data from Thredds for a particular month')
    parser.add_argument('--basedir', default='/mnt/thredds-data-00/rfc_qpe')
    _checkMonth.__name__ = "month"
    parser.add_argument('month',help="Month to aggregate, defaults to previous month",nargs="?",default=None, type=_checkMonth)
    args = parser.parse_args()
    
    fetchThenClean(args.month,threddsBase=args.basedir)
    
    
    
    
    
