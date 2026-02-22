@echo off
title REI Mod Git Automation
color 0A

set REPO_PATH=K:\MinecraftMods\RealisticRecipe\forge-1.20.1-47.4.16-mdk
set REMOTE_URL=https://github.com/hoangbussines-commits/REI.git

echo ========================================
echo    REI MOD GIT AUTOMATION
echo ========================================
echo.

cd /d %REPO_PATH%

if not exist .git (
    echo [1/6] Initializing git repository...
    git init
    git remote add origin %REMOTE_URL%
) else (
    echo [1/6] Git repository already exists.
)

echo [2/6] Checking remote connection...
git remote -v

echo [3/6] Pulling latest changes...
git pull origin main --allow-unrelated-histories

echo [4/6] Adding changes...
git add .

echo.
echo ======== CURRENT STATUS ========
git status
echo ================================
echo.

set /p COMMIT_MSG="Enter commit message: "

echo [5/6] Committing...
git commit -m "%COMMIT_MSG%"

echo [6/6] Pushing to remote...
git push origin main

echo.
echo ========================================
echo    DONE! Code pushed successfully!
echo ========================================
pause