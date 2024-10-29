log = new File(basedir, 'build.log')
test1 = new File(basedir, 'src/main/groovy/Test1.groovy')
test1Expected = new File(basedir, 'expected/src/main/groovy/Test1.groovy')
assert log.exists()
assert log.text.contains('npm-groovy-lint results in')
assert log.text.contains('linted files:')
assert test1.text == test1Expected.text

// the node_modules has not been created in the project
assert !new File(basedir, 'node_modules').exists()
assert !new File(basedir, 'package-lock.json').exists()
assert !new File(basedir, 'package.json').exists()

return true