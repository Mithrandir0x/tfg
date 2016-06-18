#!/usr/bin/python

from utils import *

if __name__ == '__main__':
    repeated_evidences = []
    while True:
        evidences = get_events()
        url = '%s' % endpoints[random.randint(0, len(endpoints) - 1)]
        path = '%s%s/%s?%s' % ( context, activityTrackingPath, environments[0], urllib.urlencode({'json': json.dumps(evidences)}) )
        for i in xrange(random.randint(1, 5 )):
            with http(url) as connection:
                print '[%s] http://%s%s' % ( i, url, path )
                try:
                    connection.request("GET", path)
                    response = connection.getresponse()
                    print response.status, response.reason
                except Exception:
                    print 700, 'I R BABOON'

        time.sleep(random.randint(1, 3))
