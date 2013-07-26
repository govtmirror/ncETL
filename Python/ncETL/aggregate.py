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
    # get one month's aggregation
    if not d:
        d = date.today() + relativedelta(months=-1)
    
    print "aggregate month ", d.strftime('%Y-%m')
    
    fetchAggregate.fetchAggregate('all', d.strftime('%Y-%m'), destDir=os.path.join(threddsBase +'archive/all_rfc'))
    
    # move the input grib files to a safe place
    gribDest = os.path.join(threddsBase,'archive/grib_files/')

    gribGlob = os.path.join(threddsBase,'realtime/files/*/NPVU_RFC_*_NWS_*_%s??.grib1' % (d.strftime('%Y%m')))

    for grib in glob.glob(gribGlob):
        fname = os.path.basename(grib)
        fdest = os.path.join(gribDest,fname) # TODO Use RFC as path element?
        os.rename(grib,fdest)
        
    # clean up the thredds artifacts?

def _checkMonth(s):
    datetime.strptime(s, '%Y-%m')
    return s

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Aggregate grid data from Thredds for a particular month')
    parser.add_argument('--basedir', default='/mnt/thredds-data-00/rfc_qpe')
    _checkMonth.__name__ = "month"
    parser.add_argument('month',help="Month to aggregate, defaults to previous month",nargs="?",default=None, type=_checkMonth)
    args = parser.parse_args()
    
    fetchThenClean(args.month,threddsBase=args.basedir)
    
    
    
