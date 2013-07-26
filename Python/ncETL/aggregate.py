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

def fetchThenClean(d, rfc, basedir, dryrun=False):
    print "Aggregating month", d.strftime('%Y-%m'), "for RFC", rfc.upper()
    
    if dryrun:
        agg = "Would fetch aggregate %s for %s to %s", (rfc, d.strftime('%Y-%m'), os.path.join(basedir +'archive/all_rfc'))
    else:
        agg = fetchAggregate.fetchAggregate(rfc, d.strftime('%Y-%m'), destDir=os.path.join(basedir +'archive/all_rfc'))
        print "Wrote aggregate as %s" % (agg)
    
    # move the input grib files to a safe place
    gribDest = os.path.join(basedir,'archive/grib_files/')

    gribGlob = os.path.join(basedir,'realtime/files/%s/NPVU_RFC_%s_NWS_*_%s??.grib1' % (rfc,rfc,d.strftime('%Y%m')))

    for grib in glob.glob(gribGlob):
        fname = os.path.basename(grib)
        fdest = os.path.join(gribDest,fname) # TODO Use RFC as path element?
        if dryrun:
            print "Would rename %s to %s" % (grib,fdest)
        else:
            os.rename(grib,fdest)
        
            print "Renamed %s to %s" % (grib,fdest)
    
    return agg

def aggregate(d, basedir='/mnt/thredds-data-00/rfc_qpe', rfc='all',dryrun=False):
    # get one month's aggregation
    if not d:
        d = date.today() + relativedelta(months=-1)
    
    if rfc == 'all':
        rfcs = ['tsju','ktua','kstr','krsa','korn','krha','kkrf','kmsr','ktar','kptr','ktir','kalr','kfwr']
    else:
        rfcs = [rfc]
        
    for rfc in rfcs:
        fetchThenClean(d, rfc, basedir,dryrun=dryrun)
        
    # clean up the thredds artifacts?

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
    aggregate(month,**optargs)
  
    
    
