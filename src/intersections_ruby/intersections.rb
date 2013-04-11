#parallelisation not working yet :(
#require "parallel"
#require "peach"

class Stretch
  attr_accessor :a, :b

  def initialize start_point, end_point
    @a = start_point
    @b= end_point
  end

  def intersects(stretch)
    (ccw(@a, @b, stretch.a) * ccw(@a, @b, stretch.b) <= 0) && (ccw(stretch.a, stretch.b, @a) * ccw(stretch.a, stretch.b, @b) <= 0)
  end

  def ccw(p_coordinate, q_coordinate, r_coordinate)
    (p_coordinate.x * q_coordinate.y - p_coordinate.y * q_coordinate.x) +
        (q_coordinate.x * r_coordinate.y - q_coordinate.y * r_coordinate.x) +
        (p_coordinate.y * r_coordinate.x - p_coordinate.x * r_coordinate.y)
  end
end

class Coordinate
  attr_accessor :x, :y

  def initialize x, y
    @x = Float x
    @y= Float y
  end
end

stretches = []
count = 0
file = File.new('Strecken_1000.dat')
start = Time.now
puts File.basename(file.path)

file.each do |line|
  points = line.split
  stretches << Stretch.new(Coordinate.new(points[0], points[1]), Coordinate.new(points[2], points[3]))
end

puts "Reading file completed in #{Time.now - start} seconds"
(0..stretches.size-1).each do |i1|
  (i1+1..stretches.size-1).each do |i2|
    if stretches[i1].intersects(stretches[i2])
      count += 1
    end
  end
end
puts "Found #{count} intersections"
puts "Calculating intersections of #{File.basename(file.path)} completed in #{Time.now - start} seconds\n\n"

