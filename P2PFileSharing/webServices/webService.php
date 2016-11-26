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
		//echo "Email: ".$emailIdFromRequest;
		//echo "Name: ".$nameFromRequest;
		//echo "Password: ".$passwordFromRequest;
		$sql = "INSERT INTO UserDetails(email,name,password) VALUES('$emailIdFromRequest','$nameFromRequest','$passwordFromRequest')";
		//echo $sql;
		if($conn->query($sql) == true){
			//echo "New record created successfully";
			
			$emailFrom = "sint.devteam@gmail.com"; //"contact@yoursite.com";
			
			//email subject
			$emailSubject = "SINT Welcome Email";

			$message = "Dear ".$nameFromRequest.",";
			$message .= "<br/><br/>Welcome to SINT!";
			$message .= "<br/><br/>You are now registered with SINT with these details";
			$message .= "<br/>Email: ".$emailIdFromRequest;
			$message .= "<br/>FullName: ".$nameFromRequest;
			$message .= "<br/><br/>Use your email id to login to SINT!";
			
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
			//echo "Email: ".$emailIdFromRequest;
			//echo "Password: ".$passwordFromRequest;
			$sql = "SELECT name FROM UserDetails WHERE email='$emailIdFromRequest' and password='$passwordFromRequest'";
			//echo $sql;
			
			$result = $conn->query($sql);

			if ($result->num_rows > 0) {
				
				$row = $result->fetch_assoc();
				echo "Success_".$row["name"];
			} else {
				//echo "0 results";
				echo "Failure";
			}
			break;
		case "Trust":
			//echo "I am inside Trust";
			$ipaddressFromRequest = $_POST["ipaddress"];
			$emailIdFromRequest   = $_POST["email"];
			
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

