import csv
import json
from cookielib import CookieJar
from urllib2 import build_opener, HTTPCookieProcessor
from cStringIO import StringIO

spreadsheet_url = 'https://docs.google.com/spreadsheet/ccc?key=13bmt8pwh4x4GFTnoctxkxjKjsxDtYwwXbGS6ZEB-ik8&output=csv'
local_json_file = 'quiz.json'


opener = build_opener(HTTPCookieProcessor(CookieJar()))
resp = opener.open(spreadsheet_url)
data = resp.read()

res = []
for question in csv.DictReader(StringIO(data)):
	res.append({
		"question": question['Android Test Question'],
		"right": question['Right Answer(s)'].split("\n"),
		"wrong": question['Wrong Answer(s)'].split("\n"),
		"tags": question['Question Tag'].split("\n"),
		"Coursera Class": question['Coursera Class']
	})

with open(local_json_file, 'w') as jsonfile:
	json.dump(res, jsonfile, indent=4, sort_keys=True)

print "Export DONE"