from flask import Flask, render_template, request

app = Flask(__name__)

PRODUCTS = {
    "Laptop": 45000,
    "Headphones": 1500,
    "Mouse": 600,
    "Keyboard": 1200,
    "Mobile": 18000,
    "USB Cable": 250
}


@app.route("/", methods=["GET", "POST"])
def home():
    bill = []
    subtotal = 0
    discount = 0
    total = 0

    if request.method == "POST":
        for product, price in PRODUCTS.items():
            quantity = request.form.get(product, "0")

            try:
                quantity = int(quantity)
            except ValueError:
                quantity = 0

            if quantity < 0:
                quantity = 0

            if quantity > 0:
                amount = price * quantity
                subtotal += amount

                bill.append({
                    "name": product,
                    "price": price,
                    "quantity": quantity,
                    "amount": amount
                })

        discount = subtotal * 0.10 if subtotal >= 5000 else 0
        total = subtotal - discount

    return render_template(
        "index.html",
        products=PRODUCTS,
        bill=bill,
        subtotal=subtotal,
        discount=discount,
        total=total
    )


if __name__ == "__main__":
    app.run(debug=True)