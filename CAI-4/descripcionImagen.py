from stegano import exifHeader
from stegano.lsbset import generators
import os
from chacha20poly1305 import ChaCha20Poly1305


secret_message = b'Hello World!'
key = os.urandom(32)
cip = ChaCha20Poly1305(key)

nonce = os.urandom(12)
ciphertext = cip.encrypt(nonce, secret_message)

secret = exifHeader.hide("./entrada.jpeg",
                        "./resultado.bmp", secret_message=secret_message)

message = exifHeader.reveal("./resultado.bmp")


print(message)