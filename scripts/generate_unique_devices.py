#!/usr/bin/python

from utils import *

if __name__ == '__main__':
    for i in xrange(1000000):
        log = get_wifi_presence(organizations, hotspots, sensors)
        print '%s,%s,%s,%s,%s' % ( log['organization'], log['startEvent'], log['hotspot'], log['sensor'], log['device'] )
