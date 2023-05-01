# 
# This PHP script will get the total number of attachments for a specific page via the Confluence REST API
# Usage:
# Update the following variables before you run this script:
# - YOUR_SERVER
# - YOUR_PERSONAL_ACCESS_TOKEN (found @ YOUR_SERVER/plugins/personalaccesstokens/usertokens.action
# - YOUR_PAGEID 
# 
# Open your CLI execute the following command:
# py get_number_of_attachments_of_a_page.py

import urllib.request
import json

REST_API = "YOUR_SERVER/rest/api/"
PERSONAL_ACCESS_TOKEN = "YOUR_PERSONAL_ACCESS_TOKEN"

def auth_request(url):
    req = urllib.request.Request(url)
    req.add_header("Authorization", "Bearer " + PERSONAL_ACCESS_TOKEN)
    req.add_header("Content-Type", "application/json")
    with urllib.request.urlopen(req) as response:
        return json.loads(response.read().decode())

def get_number_of_attachments(page_id, start=0):
    url = REST_API + "content/" + page_id + "/child/attachment?start=" + str(start) + "&limit=200"
    content = auth_request(url)
    size = content["size"]
    if "_links" in content and "next" in content["_links"]:
        size += get_number_of_attachments(page_id, start + 200)
    return size

PAGE_ID = "YOUR_PAGEID"
total_attachments = get_number_of_attachments(PAGE_ID)
print("Total Attachments: " + str(total_attachments))