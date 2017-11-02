<?php

include_once "config.php";

class Connection{
	private static $instance = null;
	private $conn = null;

	private function __construct(){
		global $config_hostIP;
		global $config_username;
		global $config_password;
		global $config_dbName;
		
		$this->conn = new mysqli($config_hostIP, $config_username, $config_password, $config_dbName);
		if ($this->conn->connect_error){
			die('Connect Error ('.$this->conn->connect_errno.') '.$this->conn->connect_error);
		}
	}
	  
	public function __destruct(){
		$this->conn->close();
	}


	public static function getInstance(){
		if($instance === null){
		$instance = new Connection();
		}
		return $instance;
	}
	
	public function query($query){
		return $this->conn->query($query);
	}
	
	public function fetch($ris){
		return $ris->fetch_array();
	}
	
	public function num_rows($ris){
		return $ris->num_rows;
	}
	
	public function getLastInsertID(){
		return $this->conn->insert_id;
	}
}

?>