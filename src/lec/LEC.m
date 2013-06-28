function radius = LEC(polygon)
disp('Calculating the largest empty circle inside a convex polygon...');
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
        % calculate normal vector
        n = [-v(2);v(1)];
        normalized_n = n/norm(n);
        a = [-normalized_n(1) -normalized_n(2) 1]
        A = [A;a]
        b = [b,(-(normalized_n(1) * p(1)) - (normalized_n(2) * p(2)))];
    end
    % transpose 1-row-matrix to vector
    b = b'
    %lb = zeros(3,1);
    [x,fval,exitflag,output,lambda] = linprog(f,A,b,[],[],[]);
    %radius = x(3);
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