import sys
import os
import commands
import fnmatch
import re
import subprocess, shlex


def cmdsplit(args):
    if os.sep == '\\':
        args = args.replace('\\', '\\\\')
    return shlex.split(args)


def cleanDirs(path):
    if not os.path.isdir(path):
        return

    files = os.listdir(path)
    if len(files):
        for f in files:
            fullpath = os.path.join(path, f)
            if os.path.isdir(fullpath):
                cleanDirs(fullpath)

    files = os.listdir(path)
    if len(files) == 0:
        os.rmdir(path)


def main():
    print("Obtaining version information from git")
    cmd = "git describe"
    try:
        process = subprocess.Popen(cmdsplit(cmd), stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=-1)
        vers, _ = process.communicate()
    except OSError:
        print("Git not found")
        vers = "v1.0-0-deadbeef"
    (major, minor, rev, githash) = re.match("v(\d+).(\d+)-(\d+)-(.*)", vers).groups()

    if os.getenv("GIT_BRANCH") is None:
        cmd = "git rev-parse --abbrev-ref HEAD"
        try:
            process = subprocess.Popen(cmdsplit(cmd), stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=-1)
            branch, _ = process.communicate()
            branch = branch.strip()
        except OSError:
            print("Git not found")
            branch = "master"
    else:
        branch = os.getenv("GIT_BRANCH").rpartition('/')[2]
        if branch == 'HEAD':
            branch = "master"

    with open("version.properties", "w") as f:
        f.write("%s=%s\n" % ("fes.major.number", major))
        f.write("%s=%s\n" % ("fes.minor.number", minor))
        f.write("%s=%s\n" % ("fes.revision.number", rev))
        f.write("%s=%s\n" % ("fes.githash", githash))
        f.write("%s=%s\n" % ("fes.branch", branch))

    print("Version information: FES %s.%s.%s (%s)" % (major, minor, rev, branch))

if __name__ == '__main__':
    main()