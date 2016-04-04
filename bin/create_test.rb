#!/usr/bin/env ruby
test_number = Dir['src/test/fixtures/default/test*.txt'].last.match(/\d+/)[0].to_i + 1
input = ''
output = ''

puts 'Введите входные данные:'
while line = gets
  input += line
end

puts 'Введите выходные данные:'
while line = gets
  output += line
end

File.open("src/test/fixtures/default/test#{test_number.to_s}.txt", 'w') { |f| f.write input }
File.open("src/test/outputs/default/test#{test_number.to_s}.txt", 'w') { |f| f.write output }
File.open("src/test/outputs/csv/test#{test_number.to_s}.txt", 'w') { |f| f.write output.gsub(' = ', ', ') }
File.open("src/test/outputs/integer/test#{test_number.to_s}.txt", 'w') { |f| f.write output.gsub(/\..*$/, '') }
