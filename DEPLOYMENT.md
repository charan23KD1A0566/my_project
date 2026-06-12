# Deployment Guide - Render.com

## Prerequisites
- GitHub account (with repository pushed)
- Render account (free at render.com)

---

## Step 1: Set Up PostgreSQL on Render ✅

1. Go to [render.com](https://render.com) → Dashboard
2. Click **"New +"** → **"PostgreSQL"**
3. Fill in details:
   - **Name:** `qr-attendance-db`
   - **Database:** `qr_attendance_db`
   - **User:** `postgres`
   - **Region:** Choose your region
   - **Plan:** Free
4. Click **"Create Database"**
5. **SAVE these values:**
   - Hostname
   - External Database URL
   - Password

---

## Step 2: Deploy Spring Boot App on Render

1. On Render Dashboard → Click **"New +"** → **"Web Service"**
2. **Connect GitHub:**
   - Click "Connect account" and authorize GitHub
   - Select your `final_qr_gps` repository
3. **Fill in deployment info:**
   - **Name:** `qr-attendance-app`
   - **Runtime:** `Java 17`
   - **Build Command:** `./mvnw clean package`
   - **Start Command:** `java -jar target/qr-attendance-management-system-0.0.1-SNAPSHOT.jar`
   - **Instance Type:** Free
   - **Region:** Same as database

4. Click **"Advanced"** → **"Add Environment Variable":**
   - `DATABASE_URL` = `jdbc:postgresql://HOST:5432/qr_attendance_db` (from Step 1)
   - `DB_USERNAME` = `postgres`
   - `DB_PASSWORD` = (your password from Step 1)

5. Click **"Create Web Service"** 🚀
6. **Wait 5-10 minutes** for deployment to complete
7. Your app will be live at: `https://qr-attendance-app.onrender.com`

---

## Step 3: Verify Deployment

Test your API endpoints:
```
GET https://qr-attendance-app.onrender.com/api/auth/health
GET https://qr-attendance-app.onrender.com/api/students
```

---

## Troubleshooting

**Build fails?**
- Check "Logs" tab in Render dashboard
- Ensure all dependencies are in `pom.xml`

**Database connection error?**
- Verify `DATABASE_URL` format: `jdbc:postgresql://HOST:5432/dbname`
- Check username/password are correct
- Database must exist first (Render creates it automatically)

**Port issues?**
- Don't set PORT in environment - app uses PORT env var automatically

---

## Important Notes

- Free tier databases sleep after 7 days of inactivity
- Render free tier services spin down with no traffic
- Upgrade to paid plan for production
