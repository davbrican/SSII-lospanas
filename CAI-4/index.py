import os
from chacha20poly1305 import ChaCha20Poly1305
import hmac
import hashlib

message = b"hello world"

print("Texto plano:", message)

key = os.urandom(32)
cip = ChaCha20Poly1305(key)

nonce = os.urandom(12)
ciphertext = cip.encrypt(nonce, message)

print("Texto cifrado:", ciphertext)

key2 = os.urandom(32)

h = hmac.new( key2, ciphertext, hashlib.sha256 )
hex = h.hexdigest()
print("hmac:", hex)


#verificación en el servidor

h2 = hmac.new( key2, ciphertext, hashlib.sha256 )
hex2 = h2.hexdigest()
print('Verificación del mensaje:',hex2==hex)

plaintext = cip.decrypt(nonce, ciphertext)   #En caso de querer descrifrar el mensaje
print("Texto plano:", plaintext)