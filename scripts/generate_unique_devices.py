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

if __name__ == '__main__':
    for i in xrange(1000000):
        log = get_wifi_presence(organizations, hotspots, sensors)
        print '%s,%s,%s,%s,%s' % ( log['organization'], log['startEvent'], log['hotspot'], log['sensor'], log['device'] )
