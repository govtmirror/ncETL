'''
Take one or more aggregated NetCDF file names as input.
Move that file to the directory where it will get automatically published,
and move the input grib files for that month and RFC to a safe place.

@author: rhayes
'''

from datetime import date
import glob
import os
import argparse
import logging
import sys
import re

basedir = '/mnt/thredds-data-00/rfc_qpe'

rfcCodes = {
# 161:'tsju',
150:'ktua',
152:'kstr',
153:'krsa',
154:'korn',
155:'krha',
156:'kkrf',
157:'kmsr',
158:'ktar',
159:'kptr',
160:'ktir',
161:'kalr',
162:'kfwr',
}

def parseFilename(fn):
    # filename = "QPE.%04d.%02d.%s.nc" % (dstart.year, dstart.month, rfcId)
    m = re.match(r"^QPE.(\d{4}).(\d{2}).(\d{3}).nc$", fn)
    if m is None:
        raise ValueError(str(fn) + " does not look like an aggregated NetCDF file (example QPE.2000.11.159.nc)")
    return (int(m.group(1)), int(m.group(2)), int(m.group(3)))

def publish(fn, basedir=basedir, dryrun=False):
    fnBase = os.path.basename(fn)
    year, month, rfcId = parseFilename(str(fnBase))
    
    d = date(year,month,1)
    
    rfc = rfcCodes.get(rfcId)
    if rfc is None:
        raise ValueError("Unknown RFC code " + str(rfcId))
    
    logging.info("Publishing aggregated data for %s for RFC %s", d.strftime('%Y-%m'), rfc.upper())
    
    destDir = os.path.join(basedir,'archive/fixed_time')
    dest = os.path.join(destDir,fnBase)
    if dryrun:
        logging.info("Would move %s to %s",fn,dest)
    else: 
        os.rename(fn,dest)
        logging.info("Moved %s to %s", fn, dest)
    
    saveGribs(rfc, d, dryrun=dryrun, basedir=basedir)

def _remove(path, dryrun=False):
    if dryrun:
        logging.info("Would remove %s", path)
    else:
        os.remove(path)
        logging.info("Removed %s", path)
 
def saveGribs(rfc,d, dryrun=False, basedir=basedir):
    
    # move the input grib files to a safe place
    gribDest = os.path.join(basedir,'archive/grib_files/')

    gribGlob = os.path.join(basedir,'realtime/files/%s/NPVU_RFC_%s_NWS_*_%s??.grib1' % (rfc.upper(),rfc.upper(),d.strftime('%Y%m')))
    logging.debug("Looking for files matching %s", gribGlob)

    for grib in glob.glob(gribGlob):
        fname = os.path.basename(grib)
        fdest = os.path.join(gribDest,fname) # TODO Use RFC as path element?
        if dryrun:
            logging.info("Would rename %s to %s",grib,fdest)
        else:
            os.rename(grib,fdest)
        
            logging.info("Renamed %s to %s",grib,fdest)
            
    # remove the THREDDS artifacts: .ncx and .gbx9 files
    ncxGlob = os.path.join(basedir,'realtime/files/%s/NPVU_RFC_%s_NWS_*_%s??.grib1.ncx' % (rfc.upper(),rfc.upper(),d.strftime('%Y%m')))
    logging.debug("Looking for files matching %s", ncxGlob)
    for ncx in glob.glob(ncxGlob):
        _remove(ncx,dryrun)
            
    gbx9Glob = os.path.join(basedir,'realtime/files/%s/NPVU_RFC_%s_NWS_*_%s??.grib1.gbx9' % (rfc.upper(),rfc.upper(),d.strftime('%Y%m')))
    logging.debug("Looking for files matching %s", gbx9Glob)
    for gbx9 in glob.glob(gbx9Glob):
        _remove(gbx9,dryrun)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Publish an aggregate NetCDF file.')
    parser.add_argument('--basedir', default=argparse.SUPPRESS, help="Base directory for THREDDS files")
    parser.add_argument('--dryrun', action="store_true")
    loglevels = [x for x in logging._levelNames.keys() if isinstance(x,basestring) and x != 'NOTSET']
    parser.add_argument('--loglevel', choices=loglevels, default='INFO')
    parser.add_argument("file", nargs="+", help="Aggregated NetCDF file", type=file)
    
    args = parser.parse_args()
    optargs = vars(args)
    
    loglevel = optargs.pop('loglevel')
    numeric_level = getattr(logging, loglevel.upper(), logging.INFO)
    logging.basicConfig(format='%(asctime)s %(levelname)s:%(message)s',filename='aggregate.log', level=numeric_level)
    
    logging.getLogger().addHandler(logging.StreamHandler(sys.stdout))
    
    ff = optargs.pop('file')
    for f in ff:
        f.close()
        publish(f.name,**optargs)
  
    
    
