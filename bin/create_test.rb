#!/usr/bin/env ruby
is_exception_test = !ARGV.nil? && (ARGV[0] == '--exception' || ARGV[0] == '-e')

input = ''
output = ''

puts 'Для окончания ввода используйте Ctrl+D.'
puts 'Введите входные данные:'
while line = gets
  input += line
end

puts 'Введите выходные данные:'
while line = gets
  output += line
end

if is_exception_test
  test_number = Dir['src/test/fixtures/exception/test*.txt'].last.match(/\d+/)[0].to_i + 1
  File.open("src/test/fixtures/exception/test#{test_number.to_s}.txt", 'w') { |f| f.write input }
else
  test_number = Dir['src/test/fixtures/default/test*.txt'].last.match(/\d+/)[0].to_i + 1
  File.open("src/test/fixtures/default/test#{test_number.to_s}.txt", 'w') { |f| f.write input }
  File.open("src/test/outputs/default/test#{test_number.to_s}.txt", 'w') { |f| f.write output }
  File.open("src/test/outputs/csv/test#{test_number.to_s}.txt", 'w') { |f| f.write output.gsub(' = ', ', ') }
  File.open("src/test/outputs/integer/test#{test_number.to_s}.txt", 'w') { |f| f.write output.gsub(/\..*$/, '') }
end
