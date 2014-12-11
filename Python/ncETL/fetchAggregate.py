import requests
import shutil
import datetime
from dateutil.relativedelta import relativedelta
import sys
import xml.etree.ElementTree as ET
import os
import logging

# test: thredds = 'http://igsarm-cida-thredds1.er.usgs.gov:8081/qa/thredds'
thredds = 'http://cida.usgs.gov/thredds/rfc_qpe'

def _makeURL(rfc):
    url = '%s/ncss/grid/fixed/qpe/realtime/%s' % (thredds,rfc.lower())
    return url

def discoverMetadata(rfc):
    url = _makeURL(rfc)+'/dataset.xml'
    
    logging.debug("Getting metadata from %s", url)
    
    try:
        resp = requests.get(url)
        # This works around an issue where the first call fails with 500 but subsequent calls work
        if resp.status_code == 500:
            resp = requests.get(url)
            
        resp.raise_for_status()
        
        dataset = ET.fromstring(resp.text)
    
        grid = dataset.find(".//gridSet/grid")
    
        idNum = grid.find("attribute[@name='Grib1_Subcenter']").get("value")
        varName = grid.get("name")
    except:
        logging.warn("Problem with metadata from %s", url)
        raise
    # Could extract a lot more meta-data here, like time bounds
    
    return (varName,idNum)

def makeArchiveName(dstart,rfcId,destDir = None):
    filename = "QPE.%04d.%02d.%s.nc" % (dstart.year, dstart.month, rfcId)
    if destDir:
        filename = os.path.join(destDir,filename)
    return filename

def fetchAggregate(rfc, month, destDir = None):
    '''
    Fetch the NetCDF file that has the aggregated rainfall data for the River Forecasting Center and month specified.
    '''
    
    logging.basicConfig(format='%(levelname)s:%(message)s', level=logging.DEBUG)

    dstart = datetime.datetime.strptime(month, '%Y-%m')
    time_start = dstart.strftime('%Y-%m-01')
    dend = dstart + relativedelta(months=1) + relativedelta(hours=-1)
    time_end = dend.strftime("%Y-%m-%dT%H:%M")

    url= _makeURL(rfc)
    varName, rfcId = discoverMetadata(rfc)
    params = {
        'var':varName,
        'time_start':time_start,
        'time_end':time_end,
        # 'addLatLon':"true"
    }
    r = requests.get(url, params=params, stream=True)
    # This works around an issue where the first call fails with 500 but subsequent calls work
    if r.status_code == 500:
        r = requests.get(url, params=params, stream=True)

    logging.info("Got response for %s", r.url)
    
    if not r.ok:
        logging.error("Error:\n%s", r.text)
        
    r.raise_for_status()
    
    filename = makeArchiveName(dstart, rfcId, destDir)
        
    with open(filename, 'wb') as fp:
        logging.info("Copying from %s to %s", r.url, fp)
        shutil.copyfileobj(r.raw, fp)
    
    return filename

if __name__ == "__main__":
    if len(sys.argv) != 3:
        raise ValueError('Usage: RFC MONTH')
    
    if sys.argv[1] == 'all':
        rfcs = ['tsju','ktua','kstr','krsa','korn','krha','kkrf','kmsr','ktar','kptr','ktir','kalr','kfwr']
        for rfc in rfcs:
            print rfc.upper()
            try:
                print fetchAggregate(rfc,sys.argv[2])
            except requests.exceptions.RequestException as e:
                print "Problem fetching for {0}: {1}".format(rfc,e)
                
    else:
        print fetchAggregate(sys.argv[1],sys.argv[2])


