/*

This script will get the total number of attachments for a specific page via the Confluence REST API

Usage:

Update the following variables before you run this script:
- YOUR_SERVER
- YOUR_PERSONAL_ACCESS_TOKEN (found @ YOUR_SERVER/plugins/personalaccesstokens/usertokens.action
- YOUR_PAGEID 

Open your CLI and execute the following command:

node get_number_of_attachments_of_a_page.js


*/

const https = require('https');

const REST_API = 'YOUR_SERVER/rest/api/';
const PERSONAL_ACCESS_TOKEN = 'YOUR_PERSONAL_ACCESS_TOKEN';
const PAGEID = 'YOUR_PAGEID';

function authHttpsGet(url) {
  const options = {
    headers: {
      Authorization: `Bearer ${PERSONAL_ACCESS_TOKEN}`
    }
  };
  return new Promise((resolve, reject) => {
    https.get(url, options, (res) => {
      let data = '';
      res.on('data', (chunk) => {
        data += chunk;
      });
      res.on('end', () => {
        resolve(JSON.parse(data));
      });
    }).on('error', (err) => {
      reject(err);
    });
  });
}

async function getNumberOfAttachments(pageid, start = 0) {
  const url = `${REST_API}content/${pageid}/child/attachment?start=${start}&limit=200`;
  const content = await authHttpsGet(url);
  let size = content.size;

  if (content._links && content._links.next) {
    size += await getNumberOfAttachments(pageid, start + 200);
  }

  return size;
}

(async function() {
  const totalAttachments = await getNumberOfAttachments(PAGEID);
  console.log(`Total Attachments: ${totalAttachments}`);
})();
