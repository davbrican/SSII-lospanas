from stegano import exifHeader
secret = exifHeader.hide("./tests/sample-files/20160505T130442.jpg",
                        "./image.jpg", secret_message="Hello world!")
print(exifHeader.reveal("./image.jpg"))