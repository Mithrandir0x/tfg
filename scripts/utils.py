
import urllib
import json
import httplib
import time
import random
import md5

# endpoints = ['kafka1.local.vm:18081', 'kafka2.local.vm:18082', 'kafka3.local.vm:18083', 'kafka4.local.vm:18084']
endpoints = ['data1.local.vm:18081', 'data2.local.vm:18082']
context = '/log-gateway'
activityTrackingPath = '/activityTracking'

organizations = [ 39159 ]
hotspots = [ 57469 ]
sensors = [ 5166, 5169, 5172, 5175, 5181, 5214, 5167, 5170, 5173, 5176, 5179, 5182, 5168, 5171, 5174, 5177, 5180 ]

environments = ['wifi']
triggers = {'presence': 3}


def device(prefix = ''):
    mac = [ "%02x" % random.randint(0, 255) for i in range(6) ]
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
        'trigger': triggers['presence'],
        'organization': organization_id,
        'startEvent': start_event,
        'hotspot': hotspot_id,
        'sensor': sensor_id,
        'device': md5_mac,
        'oui': oui,
        'tags': '',
        'power': tx_power
    }


def get_events(min = 1, max = 5):
    return { 'events': [ { 'data': get_wifi_presence(organizations, hotspots, sensors), 'meta': {} } for i in xrange(random.randint(min, max)) ] }


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