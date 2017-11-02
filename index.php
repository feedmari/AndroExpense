<?php
	include_once "utils/Connection.php";
	include_once "utils/config.php";
        include_once "gcm/send_notification.php";
	
	$dati = json_decode(file_get_contents('php://input'), true);
	
	
	
	// Controllo per la connessione instanziata da un dispositivo corretto
	if(empty($dati['DB_username']) || empty($dati['DB_password'])){
		echo json_encode(array('ok' => 'no', 'error' => 'DB_username o DB_password mancanti.'));
	} else{ // Controllo validità di DB_username e DB_password
		if($dati['DB_username'] != $config_android_username || $dati['DB_password'] != $config_android_password){
			echo json_encode(array('ok' => 'no', 'error' => 'DB_username o DB_password errati.'));
		} else{
			if(empty($dati['function'])){
				echo json_encode(array('ok' => 'no', 'error' => 'function non specificata'));
			} else{
				// Gestione dei singoli casi
				if($dati['function'] == "checkMailValidity") checkMailValidity($dati['mail']);
				elseif($dati['function'] == "insertUser") insertUser($dati['mail'], $dati['password'], $dati['username']);
				elseif($dati['function'] == "checkUserLogin") checkUserLogin($dati['mail'], $dati['password']);
				elseif($dati['function'] == "insertGroup"){
					$mail = array();
					$i = 1;
					while(!empty($dati["mail".$i])){
						array_push($mail, $dati["mail".$i]);
						$i++;
					}
					insertGroup($dati['group_name'], $dati['group_description'], $mail, $dati['me_id']);
				}
				elseif($dati['function'] == "getGroupsForUser") getGroupsForUser($dati['mail']);
				elseif($dati['function'] == "insertTransaction") insertTransaction($dati['creatorId'], $dati['groupId'], $dati['type'], $dati['details']);
				elseif($dati['function'] == "getPendingTransactions") getPendingTransactions($dati['groupId']);
				elseif($dati['function'] == "updatePendingTransaction") updatePendingTransaction($dati['transactionId'], $dati['details']);
				elseif($dati['function'] == "takeInChargeTransaction") takeInChargeTransaction($dati['transactionId'], $dati['userId']);
				elseif($dati['function'] == "getTakenTransactions") getTakenTransactions($dati['groupId'], $dati['userId']);
				elseif($dati['function'] == "completeTransaction") completeTransaction($dati['transactionId'], $dati['cost']);
				elseif($dati['function'] == "getActiveTransaction") getActiveTransaction($dati['userId'], $dati['groupId']);
				elseif($dati['function'] == "closeTransaction") closeTransaction($dati['id_debito']);
				elseif($dati['function'] == "getClosedTransaction") getClosedTransaction($dati['userId'], $dati['groupId']);
				elseif($dati['function'] == "getUsersInGroup") getUsersInGroup($dati['groupId']);
				elseif($dati['function'] == "leaveGroup") leaveGroup($dati['userId'], $dati['groupId']);
				else echo json_encode(array('ok' => 'no', 'error' => 'function non esistente'));
			} 
		}
	}
	
	// Controlla la validità della mail per un eventuale inserimento
	function checkMailValidity($mail){
		$conn = Connection::getInstance();
		$query = "SELECT * FROM Utenti WHERE mail = '$mail' ";
		$ris = $conn->query($query);		
		$row = $conn->fetch($ris);
		
		if($conn->num_rows($ris) > 0) echo json_encode(array('ok' => 'ok', 'valida' => 'no', 'user_id' => $row['id_utente']));
		else echo json_encode(array('ok' => 'ok', 'valida' => 'si'));
	}
	
	// Inserisce un nuovo utente
	function insertUser($mail, $password, $username){
		$conn = Connection::getInstance();
		$query = "INSERT INTO Utenti (id_utente, mail, password, username, iscrizione) ";
		$query .= "VALUES (NULL, '$mail', PASSWORD('$password'), '$username', CURRENT_TIMESTAMP);";
		$conn->query($query);
		
		echo json_encode(array('ok' => 'ok', 'user_id' => $conn->getLastInsertID()));
	}
	
	// Controlla il login di un utente
	function checkUserLogin($mail, $password){
		$conn = Connection::getInstance();
		$query = "SELECT * FROM Utenti ";
		$query .= "WHERE mail = '$mail' AND password = PASSWORD('$password') ";
		$ris = $conn->query($query);
		$row = $conn->fetch($ris);
		
		if($conn->num_rows($ris) == 0) echo json_encode(array('ok' => 'ok', 'valida' => 'no'));
		else echo json_encode(array('ok' => 'ok', 'valida' => 'si', 'user_id' => $row['id_utente'], 'username' => $row['username']));
	}
	
	// Inserisce il gruppo
	function insertGroup($group_name, $group_description, $users, $me_id){
		$conn = Connection::getInstance();
		$query = "INSERT INTO Gruppi (id_gruppo, nome, descrizione, creazione) ";
		$query .= "VALUES (NULL, '$group_name', '$group_description', CURRENT_TIMESTAMP);";
		$conn->query($query);
		
		$group_id = $conn->getLastInsertID();
		foreach($users as $mail){
			$query = "SELECT id_utente FROM Utenti ";
			$query .= "WHERE mail = '$mail' ";
			$ris= $conn->query($query);
			$row = $conn->fetch($ris);
			
			$query = "INSERT INTO Utente_Gruppo (id_utente_gruppo, id_utente, id_gruppo, aggiunta) ";
			$query .= "VALUES (NULL, '".$row['id_utente']."', '$group_id', CURRENT_TIMESTAMP);";
			$conn->query($query);
		}
                
                // Cerco lo username dell'utente creatore del gruppo
                $query = "SELECT username ";
                $query .= "FROM Utenti ";
                $query .= "WHERE id_utente = '$me_id' ";
                $ris = $conn->query($query);
                $row = $conn->fetch($ris);
                $me_username = $row['username'];

                // Cerco gli utenti a cui inviare la notifica
                $send_to = array();
                foreach($users as $mail){
                       $query = "SELECT id_utente ";
                       $query .= "FROM Utenti ";
                       $query .= "WHERE mail = '$mail' ";
                       $ris = $conn->query($query);
                       $row = $conn->fetch($ris);
                       $id = $row['id_utente'];

                       if($id != $me_id){
                             array_push($send_to, $id);
                       }
                }

                
                sendNotificationTo("The group ".$group_name." was created by ".$me_username , $send_to, "0", $group_id, $group_name, $group_description);

		echo json_encode(array('ok' => 'ok', 'group_id' => $group_id));		
	}
	
	// Restituisce i gruppi dell'utente
	function getGroupsForUser($mail){
		$conn = Connection::getInstance();
		$query = "SELECT G.nome AS nome_gruppo, G.descrizione AS descrizione, G.id_gruppo AS id ";
		$query .= "FROM Utenti U, Utente_Gruppo UG, Gruppi G ";
		$query .= "WHERE mail = '$mail' ";
		$query .= "AND U.id_utente = UG.id_utente ";
		$query .= "AND UG.id_gruppo = G.id_gruppo ";
		$ris = $conn->query($query);
		
		$i = 1;
		$ret = array('ok' => 'ok');
		while($row = $conn->fetch($ris)){
			$ret['nome_gruppo'.$i] = $row['nome_gruppo'];
			$ret['descrizione'.$i] = $row['descrizione'];
			$ret['id'.$i] = $row['id'];
			
			// Trovo la data dell'ultima modifica avvenuta nel gruppo
			$groupId = $row['id'];
			$query = "SELECT MAX(data) as data ";
			$query .= "FROM ( ";
			$query .= "    SELECT MAX(creazione) as data ";
			$query .= "    FROM Gruppi ";
			$query .= "    WHERE id_gruppo = '$groupId' ";
			$query .= "    UNION ALL ";
			$query .= "    SELECT MAX(apertura_ts) as data ";
			$query .= "    FROM Transazione ";
			$query .= "    WHERE id_gruppo = '$groupId' ";
			$query .= "    UNION ALL ";
			$query .= "    SELECT MAX(pic_ts) as data ";
			$query .= "    FROM Transazione ";
			$query .= "    WHERE id_gruppo = '$groupId' ";
			$query .= "    AND pic_ts IS NOT NULL ";
			$query .= "    UNION ALL ";
			$query .= "    SELECT MAX(conclusa_ts) as data ";
			$query .= "    FROM Transazione ";
			$query .= "    WHERE id_gruppo = '$groupId' ";
			$query .= "    AND conclusa_ts IS NOT NULL ";
			$query .= ") AS subQuery ";
			$ris2 = $conn->query($query);
			$row2 = $conn->fetch($ris2);
			
			$ret['lastModified'.$i] = $row2['data'];
			
			$i++;
		}
		
		echo json_encode($ret);
	}
	
	// Inserisce una nuova transazione
	function insertTransaction($creatorId, $groupId, $type, $details){
		$conn = Connection::getInstance();
		$query = "INSERT INTO Transazione (id_transazione, id_creatore, id_gruppo, tipo, dettagli, apertura_ts, id_user_pic, pic_ts, costo, conclusa_ts) ";
		$query .= "VALUES (NULL, '$creatorId', '$groupId', '$type', '$details', CURRENT_TIMESTAMP, NULL, NULL, NULL, NULL); ";
		$conn->query($query);

                // Cerco lo username dell'utente creatore del gruppo
                $query = "SELECT username ";
                $query .= "FROM Utenti ";
                $query .= "WHERE id_utente = '$creatorId' ";
                $ris = $conn->query($query);
                $row = $conn->fetch($ris);
                $me_username = $row['username'];

                // Cerco gli utenti a cui inviare la notifica
                $send_to = array();
                $query = "SELECT U.id_utente ";
                $query .= "FROM Utenti AS U, Utente_Gruppo AS UG ";
                $query .= "WHERE UG.id_gruppo = '$groupId' ";
                $query .= "AND U.id_utente = UG.id_utente ";
                $query .= "AND U.id_utente != '$creatorId' ";
                $ris = $conn->query($query);

                while($row = $conn->fetch($ris)){
                    array_push($send_to, $row['id_utente']);
                }

                //Cerco i dati del gruppo
                $query = "SELECT nome, descrizione ";
                $query .= "FROM Gruppi ";
                $query .= "WHERE id_gruppo = '$groupId' ";
                $ris = $conn->query($query);
                $row = $conn->fetch($ris);
                
                sendNotificationTo($me_username." has created a new transaction: ".$type , $send_to, "0", $groupId, $row['nome'], $row['descrizione']);
		
		echo json_encode(array('ok' => 'ok'));	
	}
	
	
	// Restituisce la lista di transazioni nello stato PENDING
	function getPendingTransactions($groupId){
		$conn = Connection::getInstance();
		$query = "SELECT id_transazione, tipo, dettagli, apertura_ts ";
		$query .= "FROM Transazione WHERE id_gruppo = '$groupId' AND id_user_pic IS NULL ";
		$ris= $conn->query($query);
		
		$i = 1;
		$ret = array('ok' => 'ok');
		while($row = $conn->fetch($ris)){
			$ret['id_transazione'.$i] = $row['id_transazione'];
			$ret['tipo'.$i] = $row['tipo'];
			$ret['dettagli'.$i] = $row['dettagli'];
			$ret['apertura_ts'.$i] = $row['apertura_ts'];
			$i++;
		}
		
		echo json_encode($ret);
	}
	
	// Aggiorna la pending transaction con i nuovi dettagli
	function updatePendingTransaction($transId, $details){
		$conn = Connection::getInstance();
		$query = "UPDATE Transazione SET dettagli = '$details' ";
		$query .= "WHERE id_transazione = '$transId';";
		$conn->query($query);
		
		echo json_encode(array('ok' => 'ok'));	
	}
	
	// Prende in carico la transazione
	function takeInChargeTransaction($transId, $userId){
		$conn = Connection::getInstance();
		$query = "UPDATE Transazione SET id_user_pic= '$userId', pic_ts = CURRENT_TIMESTAMP ";
		$query .= "WHERE id_transazione = '$transId';";
		$conn->query($query);

                // Cerco lo username dell'utente che ha preso in carico la transazione
                $query = "SELECT username ";
                $query .= "FROM Utenti ";
                $query .= "WHERE id_utente = '$userId' ";
                $ris = $conn->query($query);
                $row = $conn->fetch($ris);
                $me_username = $row['username'];

                // Cerco il nome della transazione
                $query = "SELECT tipo ";
                $query .= "FROM Transazione ";
                $query .= "WHERE id_transazione = '$transId' ";
                $ris = $conn->query($query);
                $row = $conn->fetch($ris);
                $transName = $row['tipo'];

                // Cerco gli utenti a cui inviare la notifica
                $send_to = array();
                $query = "SELECT id_utente ";
                $query .= "FROM Utenti ";
                $query .= "WHERE id_utente IN  ";
                $query .= "                   (SELECT UG.id_utente ";
                $query .= "                   FROM Transazione AS T, Utente_Gruppo AS UG ";
                $query .= "                   WHERE T.id_gruppo = UG.id_gruppo ";
                $query .= "                   AND T.id_transazione = '$transId' ";
                $query .= "                   AND UG.id_utente != '$userId' ) ";
                $ris = $conn->query($query);

                while($row = $conn->fetch($ris)){
                    array_push($send_to, $row['id_utente']);
                }
                
                //Cerco l'id del gruppo
                $query = "SELECT id_gruppo ";
                $query .= "FROM Transazione ";
                $query .= "WHERE id_transazione = '$transId' ";
                $ris = $conn->query($query);
                $row = $conn->fetch($ris);
                $groupId = $row['id_gruppo'];

                //Cerco i dati del gruppo
                $query = "SELECT nome, descrizione ";
                $query .= "FROM Gruppi ";
                $query .= "WHERE id_gruppo = '$groupId' ";
                $ris = $conn->query($query);
                $row = $conn->fetch($ris);
                
                sendNotificationTo($me_username." has taken in charge trasaction ".$transName, $send_to, "0", $groupId, $row['nome'], $row['descrizione']);
		
		echo json_encode(array('ok' => 'ok'));
	}
	
	// Restituisce la lista di transazioni nello stato TAKEN per uno specifico utente in un gruppo
	function getTakenTransactions($groupId, $userId){
		$conn = Connection::getInstance();
		$query = "SELECT id_transazione, tipo, dettagli, apertura_ts, pic_ts ";
		$query .= "FROM Transazione WHERE id_gruppo = '$groupId' ";
		$query .= "AND id_user_pic = '$userId' ";
		$query .= "AND pic_ts IS NOT NULL ";
		$query .= "AND conclusa_ts IS NULL ";
		$ris= $conn->query($query);
		
		$i = 1;
		$ret = array('ok' => 'ok');
		while($row = $conn->fetch($ris)){
			$ret['id_transazione'.$i] = $row['id_transazione'];
			$ret['tipo'.$i] = $row['tipo'];
			$ret['dettagli'.$i] = $row['dettagli'];
			$ret['apertura_ts'.$i] = $row['apertura_ts'];
			$ret['pic_ts'.$i] = $row['pic_ts'];
			$i++;
		}
		
		echo json_encode($ret);
	}
	
	// Completa la transazione
	function completeTransaction($transId, $cost){
		$conn = Connection::getInstance();
		$query = "UPDATE Transazione SET costo= '$cost', conclusa_ts = CURRENT_TIMESTAMP ";
		$query .= "WHERE id_transazione = '$transId';";
		$conn->query($query);
		
		//aggiungo questa transazione divisa equamente tra i vari componenti del gruppo
		
		//Trovo l'utente creditore
		$query = "SELECT id_user_pic ";
		$query .= "FROM Transazione ";
		$query .= "WHERE id_transazione = '$transId' ";
		$ris = $conn->query($query);
		$row= $conn->fetch($ris);
		$creditore = $row['id_user_pic'];
		
		// Cerco la lista di elementi del gruppo, diversi da colui che ha chiuso la transazione
		$query = "SELECT UG.id_utente AS debitore ";
		$query .= "FROM Utente_Gruppo AS UG, Transazione AS T ";
		$query .= "WHERE T.id_transazione = '$transId' ";
		$query .= "AND T.id_gruppo = UG.id_gruppo ";
		$query .= "AND UG.id_utente != '$creditore' ";
		$ris = $conn->query($query);
		
		$usersInGroup = intval($conn->num_rows($ris)) + 1;
		
		while($row = $conn->fetch($ris)){
			// Inserisco il debito
			$debitore = $row['debitore'];
			$ammontare = floatval($cost)/intval($usersInGroup);
			$query = "INSERT INTO Debito (id_debito, creditore, debitore, id_transazione, ammontare, saldato, saldo_ts) ";
			$query .= "VALUES (NULL, '$creditore', '$debitore', '$transId', '$ammontare', 'no', NULL); ";
			$conn->query($query);
		}


                // Cerco lo username dell'utente che ha preso in carico la transazione
                $query = "SELECT U.username, T.id_user_pic ";
                $query .= "FROM Utenti AS U, Transazione AS T ";
                $query .= "WHERE T.id_transazione = '$transId' ";
                $query .= "AND T.id_user_pic = U.id_utente ";
                $ris = $conn->query($query);
                $row = $conn->fetch($ris);
                $me_username = $row['username'];
                $userId = $row['id_user_pic'];

                // Cerco il nome della transazione
                $query = "SELECT tipo ";
                $query .= "FROM Transazione ";
                $query .= "WHERE id_transazione = '$transId' ";
                $ris = $conn->query($query);
                $row = $conn->fetch($ris);
                $transName = $row['tipo'];

                // Cerco gli utenti a cui inviare la notifica
                $send_to = array();
                $query = "SELECT id_utente ";
                $query .= "FROM Utenti ";
                $query .= "WHERE id_utente IN  ";
                $query .= "                   (SELECT UG.id_utente ";
                $query .= "                   FROM Transazione AS T, Utente_Gruppo AS UG ";
                $query .= "                   WHERE T.id_gruppo = UG.id_gruppo ";
                $query .= "                   AND T.id_transazione = '$transId' ";
                $query .= "                   AND UG.id_utente != '$userId' ) ";
                $ris = $conn->query($query);

                while($row = $conn->fetch($ris)){
                    array_push($send_to, $row['id_utente']);
                }

                //Cerco l'id del gruppo
                $query = "SELECT id_gruppo ";
                $query .= "FROM Transazione ";
                $query .= "WHERE id_transazione = '$transId' ";
                $ris = $conn->query($query);
                $row = $conn->fetch($ris);
                $groupId = $row['id_gruppo'];

                //Cerco i dati del gruppo
                $query = "SELECT nome, descrizione ";
                $query .= "FROM Gruppi ";
                $query .= "WHERE id_gruppo = '$groupId' ";
                $ris = $conn->query($query);
                $row = $conn->fetch($ris);
                
                sendNotificationTo($me_username." has completed trasaction ".$transName, $send_to, "0", $groupId, $row['nome'], $row['descrizione']);
		
			
		echo json_encode(array('ok' => 'ok'));
	}
	
	
	// Restituisce la lista di transazioni nello stato ACTIVE
	function getActiveTransaction($userId, $groupId){
		$conn = Connection::getInstance();
		$query = "SELECT T.tipo, T.dettagli, D.id_debito, D.ammontare, D.creditore AS creditore_id, D.debitore AS debitore_id ";
		$query .= "FROM Debito AS D, Transazione AS T ";
		$query .= "WHERE D.id_transazione = T.id_transazione ";
		$query .= "AND T.id_gruppo = '$groupId' ";
		$query .= "AND (D.debitore = '$userId' ";
		$query .= "     OR D.creditore = '$userId') ";
		$query .= "AND D.saldato = 'no' ";
		$ris = $conn->query($query);
		
		$i = 1;
		$ret = array('ok' => 'ok');
		while($row = $conn->fetch($ris)){
			$ret['tipo'.$i] = $row['tipo'];
			$ret['dettagli'.$i] = $row['dettagli'];
			$ret['creditore_id'.$i] = $row['creditore_id'];
			$ret['debitore_id'.$i] = $row['debitore_id'];
			$ret['id_debito'.$i] = $row['id_debito'];
			$ret['ammontare'.$i] = $row['ammontare'];
			
			$query = "SELECT username FROM Utenti WHERE id_utente = '".$row['creditore_id']."' ";
			$ris2 = $conn->query($query);
			$row2 = $conn->fetch($ris2);
			$ret['creditore_username'.$i] = $row2['username'];
			
			$query = "SELECT username FROM Utenti WHERE id_utente = '".$row['debitore_id']."' ";
			$ris2 = $conn->query($query);
			$row2 = $conn->fetch($ris2);
			$ret['debitore_username'.$i] = $row2['username'];
			
			$i++;
		}		
		
		echo json_encode($ret);
	}
	
	
	// Chiude il debito della transazione
	function closeTransaction($id_debito){
		$conn = Connection::getInstance();
		$query = "UPDATE Debito SET saldato = 'si', saldo_ts = CURRENT_TIMESTAMP ";
		$query .= "WHERE id_debito = '$id_debito' ";
		$conn->query($query);

                $query = "SELECT creditore, debitore, id_transazione ";
                $query .= "FROM Debito ";
                $query .= "WHERE id_debito = '$id_debito' ";
                $ris = $conn->query($query);
                $row = $conn->fetch($ris);
                $id_debitore = $row['debitore'];
                $id_creditore = $row['creditore'];
                $transId = $row['id_transazione'];

                $query = "SELECT username ";
                $query .= "FROM Utenti ";
                $query .= "WHERE id_utente = '$id_creditore' ";
                $ris = $conn->query($query);
                $row = $conn->fetch($ris);
                $creditore_username = $row['username'];

                $send_to = array();
                array_push($send_to, $id_debitore);

                //Cerco l'id del gruppo
                $query = "SELECT id_gruppo ";
                $query .= "FROM Transazione ";
                $query .= "WHERE id_transazione = '$transId' ";
                $ris = $conn->query($query);
                $row = $conn->fetch($ris);
                $groupId = $row['id_gruppo'];

                //Cerco i dati del gruppo
                $query = "SELECT nome, descrizione ";
                $query .= "FROM Gruppi ";
                $query .= "WHERE id_gruppo = '$groupId' ";
                $ris = $conn->query($query);
                $row = $conn->fetch($ris);
                
                sendNotificationTo($creditore_username." confirm you paid him.", $send_to, "0", $groupId, $row['nome'], $row['descrizione']);
		
		echo json_encode(array('ok' => 'ok'));	
	}
	
	// Restituisce la lista di transazioni nello stato CLOSED
	function getClosedTransaction($userId, $groupId){
		$conn = Connection::getInstance();
		$query = "SELECT T.tipo, T.dettagli, D.id_debito, D.ammontare, D.creditore AS creditore_id, D.debitore AS debitore_id, D.saldo_ts ";
		$query .= "FROM Debito AS D, Transazione AS T ";
		$query .= "WHERE D.id_transazione = T.id_transazione ";
		$query .= "AND T.id_gruppo = '$groupId' ";
		$query .= "AND (D.debitore = '$userId' ";
		$query .= "     OR D.creditore = '$userId') ";
		$query .= "AND D.saldato = 'si' ";
		$ris = $conn->query($query);
		
		$i = 1;
		$ret = array('ok' => 'ok');
		while($row = $conn->fetch($ris)){
			$ret['tipo'.$i] = $row['tipo'];
			$ret['dettagli'.$i] = $row['dettagli'];
			$ret['creditore_id'.$i] = $row['creditore_id'];
			$ret['debitore_id'.$i] = $row['debitore_id'];
			$ret['id_debito'.$i] = $row['id_debito'];
			$ret['ammontare'.$i] = $row['ammontare'];
			$ret['saldo_ts'.$i] = $row['saldo_ts'];
			
			$query = "SELECT username FROM Utenti WHERE id_utente = '".$row['creditore_id']."' ";
			$ris2 = $conn->query($query);
			$row2 = $conn->fetch($ris2);
			$ret['creditore_username'.$i] = $row2['username'];
			
			$query = "SELECT username FROM Utenti WHERE id_utente = '".$row['debitore_id']."' ";
			$ris2 = $conn->query($query);
			$row2 = $conn->fetch($ris2);
			$ret['debitore_username'.$i] = $row2['username'];
			
			$i++;
		}		
		
		echo json_encode($ret);
	}
	
	// Restituisce la lista degli utenti presenti nel gruppo passato
	function getUsersInGroup($groupId){
		$conn = Connection::getInstance();
		$query = "SELECT U.id_utente, U.mail, U.username ";
		$query .= "FROM Utente_Gruppo AS UG, Utenti AS U ";
		$query .= "WHERE UG.id_utente = U.id_utente ";
		$query .= "AND UG.id_gruppo = '$groupId' ";
		$ris= $conn->query($query);
		
		$i = 1;
		$ret = array('ok' => 'ok');
		while($row = $conn->fetch($ris)){
			$ret['id_utente'.$i] = $row['id_utente'];
			$ret['mail'.$i] = $row['mail'];
			$ret['username'.$i] = $row['username'];
			$i++;
		}
		
		echo json_encode($ret);
	}
	
	function leaveGroup($userId, $groupId){
		$conn = Connection::getInstance();
		$query = "DELETE FROM Utente_Gruppo ";
		$query .= "WHERE id_utente = '$userId' ";
		$query .= "AND id_gruppo = '$groupId' ";
		$conn->query($query);
		
		echo json_encode(array('ok' => 'ok'));
	}
?>