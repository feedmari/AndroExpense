<?php

	include_once 'GCMPushMessage.php';
	include_once 'db_functions.php';
	include_once '../utils/config.php';
	
	// Send notification passed, do $send_to users
	function sendNotificationTo($message, $send_to, $intent_id, $group_id, $group_name, $group_description){
	     
	    $gcm = new GCMPushMessage(GOOGLE_API_KEY);
	    $db = new DB_Functions();
	
	    $result = $db->getDevices();
	
	    $registatoin_ids = array();
	
	    while($row = $result->fetch_array()){
	        if(in_array($row['id_utente'], $send_to)){
	            array_push($registatoin_ids, $row['gcm_token']);
	        }
	    }
	
	    if(count($registatoin_ids) == 0){
	        $registatoin_ids['zero'] = "debug";
	    }
	
	    $gcm->setDevices($registatoin_ids);
	
	    $result = $gcm->send($message, $intent_id, $group_id, $group_name, $group_description, $data);

            return $result;
	}

?>