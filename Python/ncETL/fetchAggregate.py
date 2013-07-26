import requests
import shutil
import datetime
import sys
import xml.etree.ElementTree as ET
import os

thredds = 'http://igsarm-cida-thredds1.er.usgs.gov:8081/qa/thredds'

def discoverMetadata(rfc):
    url = '%s/ncss/grid/qpe/realtime/%s/best/dataset.xml' % (thredds,rfc.lower())
    
    resp = requests.get(url)
    
    dataset = ET.fromstring(resp.text)
    
    grid = dataset.find(".//gridSet/grid")
    
    num = grid.find("attribute[@name='Grib1_Subcenter']").get("value")
    varName = grid.get("name")
    # Could extract a lot more meta-data here, like time bounds
    
    return (varName,num)
    
def fetchAggregate(rfc, month, destDir = None):
    '''
    Fetch the NetCDF file that has the aggregated rainfall data for the River Forecasting Center and month specified.
    '''
    dt = datetime.datetime.strptime(month, '%Y-%m')
    time_start = dt.strftime('%Y-%m-01')
    time_end = "%04d-%02d-01" % (dt.year, dt.month + 1)

    url='%s/ncss/grid/qpe/realtime/%s/best' % (thredds,rfc.lower())
    varName, rfcId = discoverMetadata(rfc)
    params = {
        'var':varName,
        'time_start':time_start,
        'time_end':time_end,
        'addLatLon':"true"
    }
    r = requests.get(url, params=params, stream=True)

    if r.status_code != 200:
        raise Exception("Response code " + str(r.status_code))
    
    filename = "QPE.%04d.%02d.%s.nc" % (dt.year, dt.month, rfcId)
    if destDir:
        filename = os.path.join(destDir,filename)
        
    with open(filename, 'wb') as fp:
        shutil.copyfileobj(r.raw, fp)
    
    return filename

if __name__ == "__main__":
    if len(sys.argv) != 3:
        raise ValueError('Usage: RFC MONTH')
    
    if sys.argv[1] == 'all':
        rfcs = ['tsju','ktua','kstr','krsa','korn','krha','kkrf','kmsr','ktar','kptr','ktir','kalr','kfwr']
        for rfc in rfcs:
            print rfc.upper()
            print fetchAggregate(rfc,sys.argv[2])
    else:
        print fetchAggregate(sys.argv[1],sys.argv[2])


