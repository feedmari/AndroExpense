<?php

	include_once "../utils/Connection.php";
	
	// Utility functions
	class DB_Functions {
	 
	    private $conn = null;
	 
	    function __construct() {
	        // connecting to database
	        $this->conn = Connection::getInstance();
	    }
	    
	    // Aggiungo un nuovo utente
	    public function addUser($gcm_token, $id_utente) {
	
	        // Elimino eventuali token precedenti rimasti nel db (in caso di logout dell'utente dal dispositivo)
	        $query = "DELETE FROM gcm_ids WHERE gcm_token = '$gcm_token' ";
	        $result = $this->conn->query($query);
	
	        // Inserisco l'utente nel database
	        $query = "INSERT INTO gcm_ids (gcm_token, date_creation, id_utente) ";
	        $query .= "VALUES('$gcm_token', NOW(), '$id_utente'); ";
	        $result = $this->conn->query($query);
	        
	        if ($result){
	            return true;        
	        }
	        else {
	            return false;
	        }
	    }
	    
	    // Restituisco tutta la lista di devices presente nel db del gcm
	    public function getDevices() {
	        $result = $this->conn->query("select * FROM gcm_ids");
	        return $result;
	    }
	 
	}
?>
