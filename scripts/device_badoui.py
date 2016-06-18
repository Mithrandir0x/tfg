#!/usr/bin/python

import utils
from utils import *


def device(prefix = ''):
    mac = [ hex(random.randint(0, 255))[-2:] for i in range(6) ]
    mac_str = ':'.join(mac)
    oui = ':'.join(mac[:3])
    return '%s%s' % ( prefix, md5.new(mac_str).hexdigest() ), oui

utils.device = device


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
