import fetchAggregate

rfcs = ['tsju','ktua','kstr','krsa','korn','krha','kkrf','kmsr','ktar','kptr','ktir','kalr','kfwr']
for rfc in rfcs:
    varName, rfcCode = fetchAggregate.discoverMetadata(rfc)
    print rfc, rfcCode, varName

