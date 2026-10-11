
import tkinter as tk
from tkinter import messagebox, scrolledtext
import urllib.request
import urllib.parse
import json
import threading
import webbrowser

# Window setup
root = tk.Tk()
root.title("Recipe Finder")
root.geometry("900x700")
root.configure(bg="#FFF4E6")

recipes = []

# Header
header = tk.Frame(root, bg="#E76F51", height=100)
header.pack(fill="x")

title = tk.Label(
    header,
    text="🍲 Recipe Finder",
    font=("Arial", 26, "bold"),
    bg="#E76F51",
    fg="white"
)
title.pack(pady=(15, 2))

subtitle = tk.Label(
    header,
    text="Find delicious recipes from around the world",
    font=("Arial", 12),
    bg="#E76F51",
    fg="white"
)
subtitle.pack()

# Search area
search_frame = tk.Frame(root, bg="#FFF4E6")
search_frame.pack(pady=18)

search_entry = tk.Entry(
    search_frame,
    font=("Arial", 14),
    width=35
)
search_entry.pack(side="left", padx=8)
search_entry.insert(0, "chicken")

status_label = tk.Label(
    root,
    text="Search for a recipe to get started",
    font=("Arial", 11),
    bg="#FFF4E6",
    fg="#555555"
)
status_label.pack()

# Main content
content = tk.Frame(root, bg="#FFF4E6")
content.pack(fill="both", expand=True, padx=20, pady=10)

left_frame = tk.Frame(content, bg="white", width=280)
left_frame.pack(side="left", fill="y", padx=(0, 15))
left_frame.pack_propagate(False)

tk.Label(
    left_frame,
    text="RECIPE RESULTS",
    font=("Arial", 13, "bold"),
    bg="white",
    fg="#E76F51"
).pack(pady=10)

recipe_list = tk.Listbox(
    left_frame,
    font=("Arial", 11),
    bg="white",
    selectbackground="#E76F51",
    activestyle="none",
    width=30
)
recipe_list.pack(fill="both", expand=True, padx=8, pady=8)

right_frame = tk.Frame(content, bg="white")
right_frame.pack(side="left", fill="both", expand=True)

tk.Label(
    right_frame,
    text="RECIPE DETAILS",
    font=("Arial", 13, "bold"),
    bg="white",
    fg="#E76F51"
).pack(pady=10)

details = scrolledtext.ScrolledText(
    right_frame,
    font=("Arial", 11),
    wrap="word",
    bg="white",
    relief="flat",
    padx=12,
    pady=10
)
details.pack(fill="both", expand=True, padx=8, pady=8)


def search_recipes():
    query = search_entry.get().strip()

    if not query:
        messagebox.showwarning(
            "Missing Search",
            "Please enter a recipe name."
        )
        return

    search_button.config(state="disabled")
    status_label.config(text="Searching recipes online...")
    recipe_list.delete(0, tk.END)
    details.delete("1.0", tk.END)

    # Network work runs in a background thread
    threading.Thread(
        target=fetch_recipes,
        args=(query,),
        daemon=True
    ).start()


def fetch_recipes(query):
    try:
        url = (
            "https://www.themealdb.com/api/json/v1/1/search.php?s="
            + urllib.parse.quote(query)
        )

        request = urllib.request.Request(
            url,
            headers={"User-Agent": "PythonRecipeFinder/1.0"}
        )

        with urllib.request.urlopen(request, timeout=15) as response:
            data = json.loads(response.read().decode("utf-8"))

        meals = data.get("meals") or []

        root.after(0, lambda: show_results(meals))

    except Exception:
        root.after(0, show_error)


def show_results(meals):
    global recipes
    recipes = meals

    recipe_list.delete(0, tk.END)
    details.delete("1.0", tk.END)

    if not recipes:
        status_label.config(text="No recipes found. Try another name.")
        search_button.config(state="normal")
        return

    for meal in recipes:
        recipe_list.insert(tk.END, meal["strMeal"])

    status_label.config(
        text=f"Found {len(recipes)} recipe(s). Select one to view details."
    )

    search_button.config(state="normal")
    recipe_list.selection_set(0)
    show_details()


def show_details(event=None):
    selection = recipe_list.curselection()

    if not selection:
        return

    meal = recipes[selection[0]]

    details.delete("1.0", tk.END)

    details.insert(tk.END, meal.get("strMeal", "Recipe") + "\n\n")
    details.insert(
        tk.END,
        "Category: " + (meal.get("strCategory") or "Not available") + "\n"
    )
    details.insert(
        tk.END,
        "Cuisine: " + (meal.get("strArea") or "Not available") + "\n\n"
    )

    details.insert(tk.END, "INGREDIENTS\n")
    details.insert(tk.END, "-" * 35 + "\n")

    for i in range(1, 21):
        ingredient = meal.get(f"strIngredient{i}", "")
        measure = meal.get(f"strMeasure{i}", "")

        if ingredient and ingredient.strip():
            details.insert(
                tk.END,
                f"• {measure.strip()} {ingredient.strip()}\n"
            )

    details.insert(tk.END, "\nCOOKING INSTRUCTIONS\n")
    details.insert(tk.END, "-" * 35 + "\n\n")

    instructions = meal.get("strInstructions") or "No instructions available."
    details.insert(tk.END, instructions + "\n\n")

    details.insert(tk.END, "RECIPE IMAGE / WEBSITE\n")
    details.insert(tk.END, meal.get("strMealThumb", "Not available") + "\n\n")

    details.insert(tk.END, "Click the button below to open the online recipe.")

    open_button.config(
        command=lambda: open_recipe(meal.get("strSource"))
    )


def show_error():
    status_label.config(text="Could not connect to the recipe service.")
    search_button.config(state="normal")

    messagebox.showerror(
        "Connection Error",
        "Unable to load recipes.\n"
        "Check your internet connection and try again."
    )


def open_recipe(source):
    if source:
        webbrowser.open(source)
    else:
        messagebox.showinfo(
            "Recipe Website",
            "No external recipe website is available for this meal."
        )


# Buttons
button_frame = tk.Frame(root, bg="#FFF4E6")
button_frame.pack(pady=12)

search_button = tk.Button(
    button_frame,
    text="🔍 Search Recipes",
    font=("Arial", 12, "bold"),
    bg="#E76F51",
    fg="white",
    activebackground="#D65A3B",
    width=18,
    command=search_recipes
)
search_button.pack(side="left", padx=8)

clear_button = tk.Button(
    button_frame,
    text="Clear",
    font=("Arial", 12),
    width=10,
    command=lambda: clear_all()
)
clear_button.pack(side="left", padx=8)

open_button = tk.Button(
    button_frame,
    text="Open Recipe Website",
    font=("Arial", 11),
    bg="#2A9D8F",
    fg="white",
    command=lambda: None
)
open_button.pack(side="left", padx=8)


def clear_all():
    global recipes
    recipes = []
    search_entry.delete(0, tk.END)
    recipe_list.delete(0, tk.END)
    details.delete("1.0", tk.END)
    status_label.config(text="Search for a recipe to get started")
    open_button.config(command=lambda: None)


recipe_list.bind("<<ListboxSelect>>", show_details)
search_entry.bind("<Return>", lambda event: search_recipes())

root.mainloop()
