@echo off
title REI Mod Git Automation (MASTER)
color 0A

set REPO_PATH=K:\MinecraftMods\RealisticRecipe\forge-1.20.1-47.4.16-mdk
set REMOTE_URL=https://github.com/hoangbussines-commits/REI.git

cd /d %REPO_PATH%

echo ========================================
echo    REI MOD GIT AUTOMATION (MASTER)
echo ========================================
echo.

:: Kiểm tra git repo
if not exist .git (
    echo [1/7] Initializing git repository...
    git init
    git remote add origin %REMOTE_URL%
) else (
    echo [1/7] Git repository already exists.
)

:: Kiểm tra remote
echo [2/7] Checking remote connection...
git remote -v

:: Fetch remote
echo [3/7] Fetching remote...
git fetch origin

:: Pull từ master (lần đầu có thể lỗi, nhưng kệ)
echo [4/7] Pulling from master...
git pull origin master --allow-unrelated-histories -X theirs 2>nul

:: Add changes
echo [5/7] Adding changes...
git add . 2>nul

:: Status
echo.
echo ======== CURRENT STATUS ========
git status
echo ================================
echo.

:: Nhập commit message
set /p COMMIT_MSG="Enter commit message: "

:: Commit
echo [6/7] Committing...
git commit -m "%COMMIT_MSG%"

:: Push lên master
echo [7/7] Pushing to master...
git push -u origin master

echo.
echo ========================================
echo    DONE! Pushed to MASTER successfully!
echo ========================================
pause