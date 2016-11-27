<?php

$serverName = "localhost";
$dbUsername = "sintdevteam";
$dbPassword = "sintdevteam";
$dbName = "P2PFileSharing";

// Create connection
$conn = new mysqli($serverName, $dbUsername, $dbPassword, $dbName);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} else{
	//echo "Connection Successful";
}
//echo "I am here";
$reqQueryType = $_POST["queryType"];
$reqService   = $_POST["service"];

if($reqQueryType == "put"){
	//echo "I am inside put";
	if($reqService == "AddUser"){
		//echo "I am inside AddUser";
		$emailIdFromRequest  = $_POST["email"];
		$nameFromRequest = $_POST["name"];
		$passwordFromRequest = $_POST["password"];
		$ipaddressFromRequest = $_POST["ipaddress"];
		//echo "Email: ".$emailIdFromRequest;
		//echo "Name: ".$nameFromRequest;
		//echo "Password: ".$passwordFromRequest;
		
		// Create the keypair
		$res=openssl_pkey_new();

		// Get private key
		openssl_pkey_export($res, $privkey, "P2PFileSharing" );

		// Get public key
		$pubkey=openssl_pkey_get_details($res);
		$pubkey=$pubkey["key"];
		//var_dump($privkey);
		//var_dump($pubkey);

		$file = fopen($emailIdFromRequest.".private.key", "w");
		fwrite($file, $privkey);
		fclose($file);
		
		$sql = "INSERT INTO UserDetails(email,name,password,publicKey) VALUES('$emailIdFromRequest','$nameFromRequest','$passwordFromRequest','$pubkey')";
		//echo $sql;
		if($conn->query($sql) == true){
			//echo "New record created successfully";
			
			$sql = "INSERT INTO TrustFactorDetails(email,trustFactor,numTransactions) VALUES('$emailIdFromRequest',10,0)";
			$conn->query($sql);
			
			$sql = "INSERT INTO UserEmailIP(email,ipaddress) VALUES('$emailIdFromRequest','$ipaddressFromRequest')";
			$conn->query($sql);
			
			
			$fileName = $emailIdFromRequest.".private.key";
			$fileSize = filesize($fileName);
			$file = fopen($fileName, "r");
			$content = fread($file, $fileSize);
			fclose($file);
			unlink($fileName);
			
			//$content = chunk_split(base64_encode($content));
			
			$emailFrom = "sint.devteam@gmail.com"; //"contact@yoursite.com";
			
			//email subject
			$emailSubject = "SINT Welcome Email";

			$message = "Dear ".$nameFromRequest.",";
			$message .= "<br/><br/>Welcome to SINT!";
			$message .= "<br/><br/>You are now registered with SINT with these details";
			$message .= "<br/>Email: ".$emailIdFromRequest;
			$message .= "<br/>FullName: ".$nameFromRequest;
			$message .= "<br/><br/>Use your email id to login to SINT!";
			
			$message .= "<br/><br/>We will send you private key file shortly. You have to use it in the SINT application for authenticity.";
			
			$message .= "<br/><br/>Cheers!";
			$message .= "<br/>Team SINT";
			
			$headers = "MIME-Version: 1.0" . "\r\n"; 
			$headers .= "Content-type:text/html; charset=utf-8" . "\r\n"; 
			$headers .= "From: <$emailFrom>" . "\r\n";
			
			$retval = mail($emailIdFromRequest, $emailSubject, $message, $headers);

			// send private key file in a seperate email
			$emailSubject = "SINT | Your Private Key (Keep Safe)";
			
			$headers = "MIME-Version: 1.0" . "\r\n"; 
			$headers .= "From: <$emailFrom>" . "\r\n";
			$headers .= "Content-Type: multipart/mixed;\r\n";
			$headers .= "Content-Disposition: attachment; filename=\"".$fileName."\"\r\n";
			$emailMsg = $content;

			$retval = mail($emailIdFromRequest, $emailSubject, $emailMsg, $headers);
			if($retval == true){
				echo "Success";
			}else{
				echo "Failure";
			}

		}else{
			//echo "Error: " . $sql . "<br>" . mysqli_error($conn);
			echo "Failure";
		}
	}else if($reqService == "ForgotPassword"){
		
		$randomNumber = mt_rand(100000, 999999);
		$emailIdFromRequest  = $_POST["email"];
		
		$sql = "SELECT name FROM UserDetails WHERE email='$emailIdFromRequest'";
		$result = $conn->query($sql);
		
		if ($result->num_rows > 0) {
			$row = $result->fetch_assoc();
			$sql = "INSERT INTO SecurityCode(email,securityCode) VALUES('$emailIdFromRequest','$randomNumber')";
			if($conn->query($sql) == true){
				//echo "Random Integer: ".$randomNumber;
				$emailFrom = "sint.devteam@gmail.com"; //"contact@yoursite.com";
					
				//email subject
				$emailSubject = "SINT Password Recovery";
				
				$message = "Dear ".$row["name"].",";
				$message .= "<br/><br/>Security Code to create your new password: ".$randomNumber;
				$message .= "<br/><br/>Cheers!";
				$message .= "<br/>Team SINT";
			
				$headers = "MIME-Version: 1.0" . "\r\n"; 
				$headers .= "Content-type:text/html; charset=utf-8" . "\r\n"; 
				$headers .= "From: <$emailFrom>" . "\r\n";

				$retval = mail($emailIdFromRequest, $emailSubject, $message, $headers);
				if($retval == true){
					echo "Success";
				}else{
					echo "Failure";
				}
			}else {
				echo "EmailSentEarlier";
			}
		} else {
			//echo "0 results";
			echo "UserNotFound";
		}	
	}else if($reqService == "UpdateTrust"){
		$emailIdFromRequest  = $_POST["email"];
		$trustFactorFromRequest  = $_POST["trustFactor"];
		$numTransactionsFromRequest = $_POST["numTransactions"];
		$sql = "UPDATE TrustFactorDetails SET trustFactor='$trustFactorFromRequest', numTransactions='$numTransactionsFromRequest' WHERE email='$emailIdFromRequest'";
		if($conn->query($sql) == true){
			echo "Success";
		}else{
			echo "Failure";
		}
	}
	

}else if($reqQueryType == "get"){
	//echo "I am inside get";
	switch($reqService){
		case "QueryUser":
			$emailIdFromRequest  = $_POST["email"];
			//echo "Email: ".$emailIdFromRequest;
			//echo "Password: ".$passwordFromRequest;
			$sql = "SELECT name FROM UserDetails WHERE email='$emailIdFromRequest'";
			//echo $sql;
			
			$result = $conn->query($sql);

			if ($result->num_rows > 0) {
				// output data of each row
				echo "Success";
			} else {
				//echo "0 results";
				echo "Failure";
			}
			break;
		case "QuerySecurityCode":
			$emailIdFromRequest  = $_POST["email"];
			$securityCodeFromRequest  = $_POST["securityCode"];
			$newPasswordFromRequest = $_POST["newPassword"];
			//echo "Email: ".$emailIdFromRequest;
			$sql = "SELECT securityCode FROM SecurityCode WHERE email='$emailIdFromRequest'";
			$result = $conn->query($sql);

			if ($result->num_rows > 0) {
				$row = $result->fetch_assoc();
				if($row["securityCode"] == $securityCodeFromRequest){
					
					$sql = "DELETE FROM SecurityCode WHERE email='$emailIdFromRequest'";
					$result = $conn->query($sql);
					
					$sql = "UPDATE UserDetails SET password='$newPasswordFromRequest' WHERE email='$emailIdFromRequest'";
					$result = $conn->query($sql);
					
					// Getting FullName to display it on HomeScreen
					$sql = "SELECT name FROM UserDetails WHERE email='$emailIdFromRequest'";
					$result = $conn->query($sql);
					
					if ($result->num_rows > 0) {
						// output data of each row
						$row = $result->fetch_assoc();
						$emailFrom = "sint.devteam@gmail.com"; //"contact@yoursite.com";
					
						//email subject
						$emailSubject = "SINT Password Recovered";
						$message = "Dear ".$row["name"].",";
						$message .= "<br/><br/>Your password recovered successfully. Welcome Back!";
						$message .= "<br/><br/>Keep your password safely!";
						$message .= "<br/><br/>Cheers!";
						$message .= "<br/>Team SINT";
						
						$headers = "MIME-Version: 1.0" . "\r\n"; 
						$headers .= "Content-type:text/html; charset=utf-8" . "\r\n"; 
						$headers .= "From: <$emailFrom>" . "\r\n";

						$retval = mail($emailIdFromRequest, $emailSubject, $message, $headers);
						
						echo "Success_".$row["name"];
					} else {
						//echo "0 results";
						echo "Failure";
					}
				}else{
					echo "Failure";
				}
			} else {
				//echo "0 results";
				echo "Error";
			}
			
			break;
		case "Login":
			//echo "I am inside Login";
			$emailIdFromRequest  = $_POST["email"];
			$passwordFromRequest = $_POST["password"];
			$ipaddressFromRequest = $_POST["ipaddress"];
			//echo "Email: ".$emailIdFromRequest;
			//echo "Password: ".$passwordFromRequest;
			$sql = "SELECT name FROM UserDetails WHERE email='$emailIdFromRequest' and password='$passwordFromRequest'";
			//echo $sql;
			
			$result = $conn->query($sql);

			if ($result->num_rows > 0) {
				
				$row = $result->fetch_assoc();
				echo "Success_".$row["name"];
				
				$sql = "UPDATE UserEmailIP SET ipaddress='$ipaddressFromRequest' WHERE email='$emailIdFromRequest'";
				$result = $conn->query($sql);
				
			} else {
				//echo "0 results";
				echo "Failure";
			}
			break;
		case "TrustDetails":
			//echo "I am inside Trust"; // gets ipaddress, retrieves email, 
			$ipaddressFromRequest = $_POST["ipaddress"];
			$sql = "SELECT email FROM UserEmailIP where ipaddress='$ipaddressFromRequest'";
			$result = $conn->query($sql);
			if ($result->num_rows > 0) {
				$row = $result->fetch_assoc();
				$emailRetrieved = $row["email"];
				
				$sql = "SELECT * FROM TrustFactorDetails where email='$emailRetrieved'";
				$result = $conn->query($sql);
				if ($result->num_rows > 0) {
					$row = $result->fetch_assoc();
					$trustFactorRetrieved = $row["trustFactor"];
					$numTransactionsRetrieved = $row["numTransactions"];
				}
				$responseMsg = "Success_".$emailRetrieved."_".$trustFactorRetrieved."_".$numTransactionsRetrieved;
				echo $responseMsg;
			}else{
				echo "Failure";
			}
			break;
		case "IPList":
			$sql = "SELECT ipaddress FROM UserEmailIP";
			//echo "I am inside Email";
			
			$result = $conn->query($sql);
			if ($result->num_rows > 0) {
				$responseMsg = "Success";
				while($row = $result->fetch_assoc()) {
					$responseMsg .= "_".$row["ipaddress"];
				}
				echo $responseMsg;
			}else{
				echo "Failure";
			}
			break;
	}	
}

$conn->close();

?>

