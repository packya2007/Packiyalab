from flask import Flask, render_template, redirect, url_for, session

app = Flask(__name__)
app.secret_key = "counter_app_secret_key"


@app.route("/")
def home():
    if "count" not in session:
        session["count"] = 0

    return render_template("index.html", count=session["count"])


@app.route("/increment", methods=["POST"])
def increment():
    session["count"] = session.get("count", 0) + 1
    return redirect(url_for("home"))


@app.route("/decrement", methods=["POST"])
def decrement():
    session["count"] = session.get("count", 0) - 1
    return redirect(url_for("home"))


@app.route("/reset", methods=["POST"])
def reset():
    session["count"] = 0
    return redirect(url_for("home"))


if __name__ == "__main__":
    app.run(debug=True)