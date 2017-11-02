<?php

	include_once 'send_notification.php';
	include_once '../utils/config.php';

	$val = sendNotificationTo("ciaoo", array(63,64), "10", "caio", "we");
	
	echo("ok ".$val);

?>