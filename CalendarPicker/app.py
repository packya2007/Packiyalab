from flask import Flask, render_template, request
from datetime import datetime

app = Flask(__name__)


@app.route("/", methods=["GET", "POST"])
def home():
    selected_date = None
    formatted_date = None

    if request.method == "POST":
        date_text = request.form.get("selected_date", "")

        try:
            selected_date = datetime.strptime(
                date_text, "%Y-%m-%d"
            ).date()

            formatted_date = selected_date.strftime("%d %B %Y")

        except ValueError:
            formatted_date = "Please select a valid date."

    return render_template(
        "index.html",
        selected_date=selected_date,
        formatted_date=formatted_date
    )


if __name__ == "__main__":
    app.run(debug=True)