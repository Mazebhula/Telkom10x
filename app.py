from flask import Flask, render_template, request, redirect, url_for, flash, session

app = Flask(__name__)
app.secret_key = 'your_secret_key'

@app.route('/')
def index():
    return render_template('login.html')

@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        # Replace this with your actual authentication logic
        if username == 'admin' and password == 'password':
            session['username'] = username
            flash('Login successful!', 'success')
            return redirect(url_for('welcome'))
        else:
            flash('Invalid username or password', 'error')
            return redirect(url_for('index'))
    return render_template('login.html')

@app.route('/welcome')
def welcome():
    if 'username' in session:
        return render_template('welcome.html', username=session['username'])
    return redirect(url_for('index'))

@app.route('/logout')
def logout():
    session.pop('username', None)
    flash('You have been logged out', 'success')
    return redirect(url_for('index'))

if __name__ == '__main__':
    app.run(debug=True)