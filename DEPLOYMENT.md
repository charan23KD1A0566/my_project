# Deployment Guide - Supabase + Render.com

## Prerequisites
- GitHub account (with repository pushed)
- Supabase account (free at supabase.com)
- Render account (free at render.com)

---

## Step 1: Set Up PostgreSQL on Supabase ✅

1. Go to **supabase.com** → Sign up (free)
2. Click **"New Project"**
3. Fill in details:
   - **Project Name:** `qr-attendance-db`
   - **Database Password:** Set a strong password
   - **Region:** Choose your region
   - **Pricing Plan:** Free
4. Click **"Create new project"** (takes 2-3 minutes)
5. Once ready, go to **Settings** → **Database**
6. **SAVE these values:**
   - **Connection String (URI):** `postgresql://postgres:password@host:5432/postgres`
   - Or use individual values:
     - Host
     - Port (5432)
     - Database (postgres)
     - User (postgres)
     - Password

---

## Step 2: Deploy Spring Boot App on Render

1. On Render Dashboard → Click **"New +"** → **"Web Service"**
2. **Connect GitHub:**
   - Click "Connect account" and authorize GitHub
   - Select your `my_project` repository
3. **Fill in deployment info:**
   - **Name:** `qr-attendance-app`
   - **Runtime:** `Java 17`
   - **Build Command:** `./mvnw clean package`
   - **Start Command:** `java -jar target/qr-attendance-management-system-0.0.1-SNAPSHOT.jar`
   - **Instance Type:** Free
   - **Region:** Same as Supabase if possible

4. Click **"Advanced"** → **"Add Environment Variable":**
   - `DATABASE_URL` = `jdbc:postgresql://HOST:5432/postgres` (from Supabase Settings → Connection string)
   - `DB_USERNAME` = `postgres`
   - `DB_PASSWORD` = (your Supabase password)

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

## Supabase Connection String Formats

**Full URI (Copy from Supabase Settings):**
```
postgresql://postgres:PASSWORD@db.supabase.co:5432/postgres
```

**Spring Boot Format:**
```
jdbc:postgresql://db.supabase.co:5432/postgres
```

---

## Troubleshooting

**Build fails?**
- Check "Logs" tab in Render dashboard
- Ensure all dependencies are in `pom.xml`

**Database connection error?**
- Verify `DATABASE_URL` format
- Check username/password from Supabase Settings
- Ensure Supabase project is active

**Port issues?**
- Don't set PORT in environment - app uses PORT env var automatically
- Render provides PORT env var automatically

---

## Advantages of Supabase

✅ **Free tier never expires** (Render deletes after 90 days)  
✅ **500MB storage included** - Perfect for most projects  
✅ **Real-time capabilities** - Built-in Postgres with real-time subscriptions  
✅ **Always-on** - No auto-sleep with free tier  
✅ **Easy scaling** - Upgrade anytime  

---

## Cost Breakdown

| Service | Cost |
|---------|------|
| Supabase (Free) | FREE forever (500MB) |
| Render (Free) | FREE (then paid) |
| **Total** | **FREE** ✅ |

Upgrade to paid when you need more storage or higher performance.

