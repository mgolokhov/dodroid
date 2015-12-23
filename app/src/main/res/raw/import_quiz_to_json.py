import csv
import json
import sys
from cookielib import CookieJar
from urllib2 import build_opener, HTTPCookieProcessor
from cStringIO import StringIO
from difflib import SequenceMatcher

spreadsheet_url = 'https://docs.google.com/spreadsheet/ccc?key=13bmt8pwh4x4GFTnoctxkxjKjsxDtYwwXbGS6ZEB-ik8&output=csv'
local_json_file = 'quiz.json'

opener = build_opener(HTTPCookieProcessor(CookieJar()))
resp = opener.open(spreadsheet_url)
data = resp.read()

res = []
for index, question in enumerate(csv.DictReader(StringIO(data))):
	if question['ID']:
		res.append({
			"ID": index,
			"question": question['Android Test Question'],
			"right": [i for i in question['Right Answer(s)'].split("\n") if i],
			"wrong": [i for i in question['Wrong Answer(s)'].split("\n") if i],
			"tags": [i for i in question['Question Tag'].split("\n") if i],
			"docRef" : question["Reference Link"],
		})


# cannot import local modules like
# from checked_questions import reviewed
# cached pyc files brake gradle =(
with open('reviewed_questions.py') as f:
	exec(f.read())

exit_by_dupl = False
for index, i in enumerate(res):
	for j in res[index+1:]:
		q1 = i['question']
		q2 = j['question']
		ratio = SequenceMatcher(None, q1, q2).ratio()
		maybe = (ratio, q1, q2)
		if ratio > .5 and maybe not in reviewed:
		 	print repr(maybe) + ","
		 	exit_by_dupl = True

if exit_by_dupl:
	sys.exit("\nFound possible duplicates, exit...")

with open(local_json_file, 'w') as jsonfile:
	json.dump(res, jsonfile, indent=4, sort_keys=True)

print "Imported {} questions".format(len(res))


