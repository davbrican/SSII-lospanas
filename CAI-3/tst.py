from os import urandom
from Crypto.Cipher import AES


# For Generating cipher text
secret_key = urandom(16)
iv = urandom(16)
obj = AES.new(secret_key, AES.MODE_CBC, iv)

# Encrypt the message
message = b'Lorem Ipsum text'
print('Original message is: ', message)
encrypted_text = obj.encrypt(message)
print('The encrypted text', encrypted_text)

# Decrypt the message
rev_obj = AES.new(secret_key, AES.MODE_CBC, iv)
decrypted_text = rev_obj.decrypt(encrypted_text)
print('The decrypted text', decrypted_text.decode('utf-8'))