

/***
 * An implementation of an enumeration which unique identifies the purpose of a Message.
 * An enumeration was used since it ensures that only the given codes can be used increasing the Security of Messages.
 * Additionally, it increases compile-time checking increasing run-time efficiency.
 */
public enum MessageIDentity {
	/* REGISTRATION_REQUEST = 0 (From Client to Server Only)
	 * REGISTRATION_RESPONSE = 1 (From Server to Client Only)
	 * TEXT_TRANSFER_REQUEST = 2 (From Client to Server Only)
	 * TEXT_TRANSFER_RECEIPT = 3 (From Server to Client Only)
	 * IMAGE_TRANSFER_REQUEST = 4 (From Client to Server Only)
	 * IMAGE_TRANSFER_CONFIRMATION_REQUEST = 5 (From Server to Client Only)
	 * IMAGE_TRANSFER_CONFIRMATION_RESPONSE = 6 (From Client to Server Only)
	 * IMAGE_TRANSFER_RECEIPT = 7 (From Server to Client Only)
	 * MESSAGE_CONFIRMATION_RESPONSE = 8 (From Client to Server Only) - To check if the message was delivered
	 * MESSAGE_CONFIRMATION_RECEIPT = 9 (From Server to Client Only) - To check if the message was delivered
	 * ONLINE_CLIENTS_REQUEST = 10 (From Client to Server only) - To ask for online client's details
	 * ONLINE_CLIENTS_RESPONSE = 11 (From Server to Client only) - To give the client the online client's details
	 * AUDIO_TRANSFER_REQUEST = 12 (From Client to Server Only)
	 * AUDIO_TRANSFER_CONFIRMATION_REQUEST = 13 (From Server to Client Only)
	 * AUDIO_TRANSFER_CONFIRMATION_RESPONSE = 14 (From Client to Server Only)
	 * AUDIO_TRANSFER_RECEIPT = 15 (From Server to Client)
	 * More Details on Message Code Schematic on Google Drive
	 */

	REGISTRATION_REQUEST, REGISTRATION_RESPONSE, TEXT_TRANSFER_REQUEST, TEXT_TRANSFER_RECEIPT, TEXT_SEND_TO_ALL_REQUEST,
	TEXT_SEND_TO_ALL_RECEIPT, IMAGE_TRANSFER_REQUEST, IMAGE_TRANSFER_CONFIRMATION_REQUEST, IMAGE_TRANSFER_CONFIRMATION_RESPONSE,
	IMAGE_TRANSFER_RECEIPT, IMAGE_SEND_TO_ALL_REQUEST, ONLINE_CLIENTS_REQUEST, ONLINE_CLIENTS_RESPONSE, CLOSE_CONNECTION,AUDIO_TRANSFER_REQUEST,
	AUDIO_TRANSFER_CONFIRMATION_REQUEST, AUDIO_TRANSFER_CONFIRMATION_RESPONSE,AUDIO_TRANSFER_RECEIPT, AUDIO_CONFIRMATION_REQUEST_TEXT;
}
