% Compute the largest empty circle (LEC)

function LEC(polygon)

    disp 'Calculating the largest empty circle inside a convex polygon...';

    % detect if first and last coordinates are same and fix matrix if needed
    validatedPolygon = validate(polygon);

    [rows,columns]= size(validatedPolygon);
    
    f = [0; 0; -1];
    A = [];
    b = [];
    
    % Iterate trough matrix (polygon coordinates)
    for i = 1 : (rows - 1)
        
        % calculate vector from point p to q
        p = validatedPolygon(i,:)';
        q = validatedPolygon(i + 1,:)';
        v = q - p; % "head-minus-tail-rule"
        
        % calculate normal vector (orthogonal to vector v)
        n = -[-v(2);v(1)];
        
        % normailze vector n (vector length of 1, but some direction)
        normalized_n = n/norm(n);
        
        a = [-normalized_n(1) -normalized_n(2) 1];
        A = [A;a];
        b = [b,(-(normalized_n(1) * p(1)) - (normalized_n(2) * p(2)))];        
    end
    
    % transpose 1-row-matrix to vector
    b = b';
    
    % use simplex algorithm to solve the linear program
    opts.Algorithm = 'simplex';
    [x,fval,exitflag,output,lambda] = linprog(f,A,b,[],[],[],[],[],opts);
    
    printSolution(abs(x));
    plotSolution(polygon,x);
end

function validPolygon = validate(polygon)
    [rows,columns]= size(polygon);
    if(polygon(1,:) == polygon(rows,:))
        % cut off last row
        validPolygon = polygon(1:rows-1,:);
    else
        validPolygon = polygon;
    end
end

function printSolution(x)
    disp 'Cicle coordinates:';
    disp(x(1))
    disp(x(2))
    disp 'Circle radius:';
    disp(abs(x(3)))
end

% plot polygon with inscribed circle
function plotSolution(polygon,x)
    fill(polygon(:,1),polygon(:,2),'b');
    hold on;
    drawCircle(x(1),x(2),abs(x(3)));
    radius = abs(x);
end

% draw filled circle
function drawCircle(x,y,r)
    ang=0:0.01:2*pi; 
    xp=r*cos(ang);
    yp=r*sin(ang);
    fill(x+xp,y+yp,'--g');
end