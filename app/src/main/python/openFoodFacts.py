import openfoodfacts
import time

def getFoodInfo():
    barcode = "8410076601261"
    product = ""
    while product=="":
        try:
            product = openfoodfacts.products.get_product(barcode)
        except:
            time.sleep(5)
            continue


    return product.get('product').get('product_name')

def hello():
    return "hello world"
