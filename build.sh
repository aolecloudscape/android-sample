# This will automatically run in production deploy
git submodule init
git submodule update
make touchrst
make html

