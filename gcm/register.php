<?php

	include_once 'db_functions.php';
	
	// Store user details in db
	if (isset($_POST["gcm_token"]) && isset($_POST["id_utente"])) {
	
	    $gcm_token = $_POST["gcm_token"];
	    $id_utente = $_POST["id_utente"];
	    
	 
	    $db = new DB_Functions();
	 
	    $res = $db->addUser($gcm_token, $id_utente);
	
	    if ($res){
	        $response['message'] = 'Utente registrato!';
	        $response['success'] = 1;
	    }
	    else{
	        $response['message'] = 'Utente non registrato!';
	        $response['success'] = 0;
	    }
	
	} else {
	    $response['message'] = "Errore!";
	    $response['success'] = 0;
	}
	
	
	echo json_encode($response);    
	
?>