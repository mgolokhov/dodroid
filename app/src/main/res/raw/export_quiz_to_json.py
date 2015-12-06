# cannot import local modules like
# from checked_questions import reviewed
# cached pyc files brake gradle =(
reviewed = (
	(0.5185185185185185, 'A stopped fragment is not visible.', 'Activity or Fragment becomes visible after call'),
	(0.5217391304347826, 'Which layout does not arrange its children in any particular manner', 'Which layouts support assigning a weight to individual children with the android:layout_weight'),
	(0.7375886524822695, 'Which attribute sets the gravity of the content of the View its used on?', 'Which attribute sets the gravity of the View or Layout in its parent?'),
	(0.6596858638743456, "_____ is the space inside the border, between the border and the actual view's content.", 'The space outside the border, between the border and the other elements next to this view are defined by'),
	(0.5467625899280576, 'All Java SE classes are available to Android programs', 'All Java features (libraries, language-syntax) are available by default in Android SDK'),
	(0.5897435897435898, 'All widgets extend View class', 'All widgets containers (layouts) extend ViewGroup'),
	(0.5426356589147286, 'Which of the following are subclasses of the ViewGroup?', 'Which of the following are true statements about LayoutInfalater.inflate()'),
	(0.5151515151515151, 'Which of the following are subclasses of the ViewGroup?', 'Which of the following are true statements about AlertDialog. Dialog can show'),
	(0.6785714285714286, 'Which of the following are subclasses of the ViewGroup?', 'Which of the following are examples of User Notification?'),
	(0.5217391304347826, 'Fragments can be used without activities', 'Fragments were introduced in '),
	(0.5409836065573771, 'Fragments can be used without activities', 'Fragments inside the host Activity can be started even if the Activity is stopped '),
	(0.5274725274725275, 'Fragments can be used without activities', 'A handler can be associated with a multiple threads'),
	(0.5538461538461539, 'Android operating system is based on', 'Android user interface can be'),
	(0.8809523809523809, 'The default <LinearLayout> orientation is', 'The default <RelativeLayout> orientation is'),
	(0.5142857142857142, 'By default, all child views of RelativeLayout are drawn at the', 'The default <RelativeLayout> orientation is'),
	(0.5154639175257731, 'How you can identify a fragment?', 'Only FrameLayout can be a placeholder container for your fragment'),
	(0.6229508196721312, 'How you can identify a fragment?', 'You can set a Fragment tag by'),
	(0.7435897435897436, 'Check true statements about ListActivity', 'Check true statements about RadioGroup'),
	(0.6823529411764706, 'Check true statements about ListActivity', 'Check true statements (if any) about Fragment'),
	(0.6885245901639344, 'Check true statements about ListActivity', 'Check true statements'),
	(0.5098039215686274, 'Check true statements about ListActivity', 'Choose true statements for method Fragment.setRetainInstance()'),
	(0.7152317880794702, 'Which of the following are true statements about LayoutInfalater.inflate()', 'Which of the following are true statements about AlertDialog. Dialog can show'),
	(0.5225225225225225, 'Which of the following are true statements about LayoutInfalater.inflate()', 'Inflation (LayoutInflater.inflate) is'),
	(0.6987951807228916, 'Check true statements about RadioGroup', 'Check true statements (if any) about Fragment'),
	(0.711864406779661, 'Check true statements about RadioGroup', 'Check true statements'),
	(0.5043478260869565, 'Check true statements about RadioGroup', 'Which of the following are true statements about AlertDialog. Dialog can show'),
	(0.6363636363636364, 'Check true statements (if any) about Fragment', 'Check true statements'),
	(0.5981308411214953, 'Check true statements (if any) about Fragment', 'Choose true statements for method Fragment.setRetainInstance()'),
	(0.6981132075471698, 'You can have multiple Activities in the foreground', 'Android app can run multiple processes in the foreground'),
	(0.7786259541984732, 'Which dimension unit is recommend when specifying font sizes', 'Which dimension units are recommend when specifying margins and padding'),
	(0.5225225225225225, 'Java Threads running in the same Process share', 'Application components in the same process use the same UI thread'),
	(0.5283018867924528, 'Java Threads running in the same Process share', 'Components of different applications run in the same process'),
	(0.5736434108527132, '(True or False) Starting with API 11 (Android 3.0) AsyncTasks can operate in parallel.', '(True or False) AsyncTask can be cancelled.'),
	(0.5454545454545454, "Which one of the following HTTP clients is Android's preferred HTTP client?", 'Which of the following are examples of User Notification?'),
	(0.6, 'You may use a fragment without its own UI', 'You can set a Fragment tag by'),
	(0.512, 'Application components in the same process use the same UI thread', 'Components of different applications run in the same process'),
)


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
for question in csv.DictReader(StringIO(data)):
	if question['ID']:
		res.append({
			"ID": question['ID'],
			"question": question['Android Test Question'],
			"right": [i for i in question['Right Answer(s)'].split("\n") if i],
			"wrong": [i for i in question['Wrong Answer(s)'].split("\n") if i],
			"tags": [i for i in question['Question Tag'].split("\n") if i],
			"Coursera Class": question['Coursera Class'],
			"docRef" : question["Reference Link"],
		})

for index, i in enumerate(res):
	for j in res[index+1:]:
		q1 = i['question']
		q2 = j['question']
		ratio = SequenceMatcher(None, q1, q2).ratio()
		maybe = (ratio, q1, q2)
		if ratio > .5 and maybe not in reviewed:
		 	print repr(maybe) + ","
		 	sys.exit("\nFound possible duplicates, exit...")

with open(local_json_file, 'w') as jsonfile:
	json.dump(res, jsonfile, indent=4, sort_keys=True)

print "Export DONE"