#!/usr/bin/python

import httplib
import urllib
import json
import time
import random
import md5

endpoints = ['kafka1.local.vm:18081', 'kafka2.local.vm:18082', 'kafka3.local.vm:18083', 'kafka4.local.vm:18084']
context = '/log-gateway'
activityTrackingPath = '/activityTracking'

platforms = ['wifi']
events = { 'presence': 3 }

organizations = [ 39159 ]
hotspots = [ 57469 ]
sensors = [ 5166, 5169, 5172, 5175, 5181, 5214, 5167, 5170, 5173, 5176, 5179, 5182, 5168, 5171, 5174, 5177, 5180 ]

def device(prefix = ''):
    mac = [ hex(random.randint(0, 255))[-2:] for i in range(6) ]
    mac_str = ':'.join(mac)
    oui = ':'.join(mac[:3])
    return '%s%s' % ( prefix, md5.new(mac_str).hexdigest() ), oui

def get_wifi_presence(organizations, hotspots, sensors):
    organization_id = organizations[random.randint(0, len(organizations) - 1)]
    start_event = int(time.time())
    hotspot_id = hotspots[random.randint(0, len(hotspots) - 1)]
    sensor_id = sensors[random.randint(0, len(sensors) - 1)]
    md5_mac, oui = device()
    tx_power = random.randint(-126, -2)
    return {
        'event': events['presence'],
        'organization': organization_id,
        'startEvent': start_event,
        'hotspot': hotspot_id,
        'sensor': sensor_id,
        'device': md5_mac,
        'oui': oui,
        'tags': '',
        'power': tx_power
    }

class http(object):
    """docstring for http_get"""
    def __init__(self, url):
        self.url = url
        self.connection = None
    def __enter__(self):
        try:
            self.connection = httplib.HTTPConnection(self.url)
        except Exception:
            return None
        return self.connection
    def __exit__(self, type, value, traceback):
        if self.connection:
            self.connection.close()

if __name__ == '__main__':
    while True:
        evidences = { 'events': [ { 'paramsValues': get_wifi_presence(organizations, hotspots, sensors), 'extraParams': {} } for i in xrange(random.randint(1, 1)) ] }
        url = '%s' % endpoints[random.randint(0, 0)]
        with http(url) as connection:
            path = '%s%s/%s?%s' % ( context, activityTrackingPath, platforms[0], urllib.urlencode({'json': json.dumps(evidences)}) )
            print 'http://%s%s' % ( url, path )
            try:
                connection.request("GET", path)
                response = connection.getresponse()
                print response.status, response.reason
            except Exception:
                print 700, 'I R BABOON'

        time.sleep(random.randint(1, 3))


# /log-gateway/activityTracking/wifi?json={"events": [{"paramsValues": {"organization_id": 39159, "hotspot_id": 57469, "oui": "c9:x3:83", "sensor_id": 5173, "power": -106, "start_event": 1461613808, "md5_mac": "7c6834f3d36730fec3a0d268e6b1bd51", "event": 3}, "extraParams": {}}]}