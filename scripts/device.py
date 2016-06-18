#!/usr/bin/python

from utils import *

if __name__ == '__main__':
    while True:
        evidences = get_events()
        url = '%s' % endpoints[random.randint(0, len(endpoints) - 1)]
        with http(url) as connection:
            path = '%s%s/%s?%s' % (context, activityTrackingPath, environments[0], urllib.urlencode({'json': json.dumps(evidences)}))
            print 'http://%s%s' % ( url, path )
            try:
                connection.request("GET", path)
                response = connection.getresponse()
                print response.status, response.reason
            except Exception:
                print 700, 'I R BABOON'

        time.sleep(random.randint(1, 3))
