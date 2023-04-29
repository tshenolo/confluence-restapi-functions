<?php
/*

This PHP script will get the total number of attachments for a specific page via the Confluence REST API

Update the following variables before you run this script:
- YOUR_SERVER
- YOUR_PERSONAL_ACCESS_TOKEN (found @ YOUR_SERVER/plugins/personalaccesstokens/usertokens.action
- YOUR_PAGEID 
*/

define("REST_API", "YOUR_SERVER/rest/api/");
define("PERSONAL_ACCESS_TOKEN", "YOUR_PERSONAL_ACCESS_TOKEN");

function auth_file_get_contents($url){
    $context = stream_context_create(array(
        'http' => array(
            'header'  => "Authorization: Bearer ".PERSONAL_ACCESS_TOKEN,
            'ignore_errors' => true
        ),"ssl" => array(
            "verify_peer" => false,
            "verify_peer_name" => false,
          )
    ));
    return file_get_contents($url, false, $context);
}

function getNumberOfAttachments($pageid, $start = 0){
    $url = REST_API."content/$pageid/child/attachment?start=$start&limit=200";
    $jsoncontent = auth_file_get_contents($url);
    $content = json_decode($jsoncontent);
    $size = $content->size; 

    if( isset($content->_links->next)){
        $size = $size+getNumberOfAttachments($pageid, $start+200);    
    }

    return $size;
}

$PAGEID = "YOUR_PAGEID";
echo "Total Attachments: ".getNumberOfAttachments($PAGEID);