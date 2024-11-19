<script>
  import CompareColumnsCard from './CompareColumnsCard.svelte';
  import HeaderTypeAssociation from './HeaderTypeAssociation.svelte';
  import ColumnsOrder from './ColumnsOrder.svelte';
  import RangeWithWord from './RangeWithWord.svelte';
  import DateRangeRules from './DateRangeRules.svelte';


</script>

<div class="space-y-6">
  <div class="bg-zinc-800/50 p-4 rounded-lg space-y-4">
    <HeaderTypeAssociation />
    <CompareColumnsCard />
    <ColumnsOrder />
    <RangeWithWord />
    <DateRangeRules />
  </div>
</div>