#!/usr/bin/python

# import unicodecsv # Install this if you need it.
import csv
import glob
import re
import sys

allQueries = []
parseStartRe = "Parsing command: (.+)"
lineStartRe = "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2},\d{3}"
files = glob.glob("/var/log/hive/hive-server2*")
files.sort(reverse=True)
for f in files:
	with open(f) as fd:
		inQuery = False
		query = []
		for line in fd:
			if inQuery:
				m = re.match(lineStartRe, line)
				if m:
					thisQuery = "".join(query)
					allQueries.append(thisQuery)
					query = []
					inQuery = False
				else:
					query.append(line)
			else:
				res = re.search(parseStartRe, line)
				if res:
					query = [ res.group(1) ]
					inQuery = True

writer = csv.writer(sys.stdout)
for query in allQueries:
	writer.writerow([query])
