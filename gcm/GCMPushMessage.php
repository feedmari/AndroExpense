<?php

	// Class used to send push notifications using Google Cloud Messaging for Android
	class GCMPushMessage {
	
		private $url = 'https://gcm-http.googleapis.com/gcm/send';
		private $serverApiKey;
		private $devices = array();
		
		// Costruttore
		function __construct($apiKeyIn){
			$this->serverApiKey = $apiKeyIn;
		}
		
		// Controlla che deviceIds sia un array, altrimenti lo crea
		function setDevices($deviceIds){
			if(is_array($deviceIds)){
				$this->devices = $deviceIds;
			} else {
				$this->devices = array($deviceIds);
			}
		}
	
		// Send the message to the device
		function send($message, $id_intent, $id_gruppo, $nome_gruppo, $descrizione_gruppo, $data = false, $collapse_key = null, $time_to_live = null){
			
			if(!is_array($this->devices) || count($this->devices) == 0){
				$this->error("No devices set");
			}
			
			if(strlen($this->serverApiKey) < 8){
				$this->error("Server API Key not set");
			}
			
			$fields = array(
				'registration_ids'  => $this->devices,
				'data'              => array( "message" => $message , "id_intent" => $id_intent, "id_gruppo" => $id_gruppo, "nome_gruppo" => $nome_gruppo, "descrizione_gruppo" => $descrizione_gruppo),
			);
	
			if ($time_to_live != null) {
				$fields['time_to_live'] = $time_to_live; 
			}
	
			if ($collapse_key != null) {
				$fields['collapse_key'] = $collapse_key;
			}
			
			if(is_array($data)){
				foreach ($data as $key => $value) {
					$fields['data'][$key] = $value;
				}
			}
	
			$headers = array( 
				'Authorization: key=' . $this->serverApiKey,
				'Content-Type: application/json'
			);
	
			// Open connection
			$ch = curl_init();
			
			// Set the url, number of POST vars, POST data
			curl_setopt( $ch, CURLOPT_URL, $this->url );
			
			curl_setopt( $ch, CURLOPT_POST, true );
			curl_setopt( $ch, CURLOPT_HTTPHEADER, $headers);
			curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
			
			curl_setopt( $ch, CURLOPT_POSTFIELDS, json_encode( $fields ) );
			
			// Avoids problem with https certificate
			curl_setopt( $ch, CURLOPT_SSL_VERIFYHOST, false);
			curl_setopt( $ch, CURLOPT_SSL_VERIFYPEER, false);
			
			// Execute post
			$result = curl_exec($ch);
			
			// Close connection
			curl_close($ch);
			
			return $result;
		}
		
		function error($msg){
			echo "Android send notification failed with error:";
			echo "\t" . $msg;
			exit(1);
		}
	}
?>