import angular from 'angular'

require('./node_modules/angular-mocks/angular-mocks.js');
require('./node_modules/bootstrap/dist/css/bootstrap.css');
/**
 * `require` all modules in the given webpack context
 */
function requireAll(context) {
  context.keys().forEach(context);
}
// Collect all angular modules
requireAll(require.context(
        './app',
        /* use subdirectories: */ true,
        /^(?!.*(spec.js$|e2e.js)).*\.js$/
));

requireAll(require.context(
        './app/view',
        /* use subdirectories: */ true,
        /\.html$/
));

requireAll(require.context(
        './app',
        /* use subdirectories: */ true,
        /\.css$/
));
